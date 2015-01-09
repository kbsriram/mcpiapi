package com.kbsriram.mcpi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class ClientHandler
    extends Thread
{
    ClientHandler(String name, CommandServer cs, Socket cli)
    {
        super(name);
        m_cs = cs;
        m_cli = cli;
    }

    final void stopClient()
    {
        try { m_cli.close(); }
        catch (Throwable ign) {}
        try { m_rets.offer(ICommandHandler.KILL); }
        catch (Throwable ign) {}
    }

    @Override
    public void run() {
        try { handle(); }
        catch (Throwable ex) {
            s_logger.debug("Mcpi client terminated.", ex);
        }
        finally {
            m_running = false;
            try { m_cli.close(); }
            catch (Throwable ign) {}
            try { m_cs.removeClient(this); }
            catch (Throwable ign) {}
            s_logger.info("Client "+this+" disconnected.");
        }
    }

    final Command poll()
    { return m_commands.poll(); }

    final void enqueueReturn(String ret)
    {
        if (m_running) {
            try { m_rets.put(ret); }
            catch (InterruptedException ign) {}
        }
    }

    private final void handle()
        throws Exception
    {
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            br = new BufferedReader
                (new InputStreamReader(m_cli.getInputStream()));
            pw = new PrintWriter
                (new BufferedWriter
                 (new OutputStreamWriter(m_cli.getOutputStream())));

            String line;
            while ((line = br.readLine()) != null) {
                s_logger.debug("Got `"+line+"'");
                Command cmd = parse(line, pw, this);
                if (cmd != null) {
                    try {
                        s_logger.debug("Waiting for: "+cmd);
                        m_commands.put(cmd);
                        String resp = m_rets.take();
                        s_logger.debug("Response: "+resp);
                        if (ICommandHandler.KILL.equals(resp)) {
                            return;
                        }
                        if (!ICommandHandler.VOID.equals(resp)) {
                            pw.print(resp);
                            pw.print("\n");
                            pw.flush();
                        }
                    }
                    catch (InterruptedException ign) {}
                }
            }
        }
        finally {
            if (br != null) {
                try { br.close(); }
                catch (Exception ign) {}
            }
            if (pw != null) {
                try { pw.close(); }
                catch (Exception ign) {}
            }
        }
    }
    private final CommandServer m_cs;
    private final Socket m_cli;
    private final BlockingQueue<Command> m_commands =
        new ArrayBlockingQueue<Command>(1);
    private final BlockingQueue<String> m_rets =
        new ArrayBlockingQueue<String>(1);
    private boolean m_running = true;

    private final static Command parse
        (String line, PrintWriter err, ClientHandler h)
    {
        Matcher m = s_cmd_pattern.matcher(line);
        if (!m.matches()) {
            err.print("Unknown command: `"+line+"'");
            err.print("\n");
            err.flush();
            return null;
        }
        return new Command(h, m.group(1), m.group(2).split(","));
    }
    private final static Pattern s_cmd_pattern = Pattern.compile
        ("([a-zA-Z\\.]+)\\s*\\((.*)\\)");
    private final static Logger s_logger = LogManager.getLogger();
}
