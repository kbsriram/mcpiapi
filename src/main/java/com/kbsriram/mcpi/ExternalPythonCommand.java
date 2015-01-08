package com.kbsriram.mcpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

final class ExternalPythonCommand
    implements ICommand
{
    public ExternalPythonCommand()
    {
    }

    @Override
    public String getCommandName()
    { return "python"; }

    @Override
    public String getCommandUsage(ICommandSender sender)
    { return "python command [args...]"; }

    @Override
    public List getCommandAliases()
    { return s_aliases; }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0) {
            sendError(sender, "Invalid arguments");
            return;
        }

        if (!s_safearg.matcher(args[0]).matches()) {
            sendError(sender, "Invalid module name: `"+args[0]+"'");
            return;
        }

        // Check file exists.
        File moddir;
        try { moddir = (new File("mcpimods/python")).getCanonicalFile(); }
        catch (IOException ioe) {
            sendError
                (sender, "Unable to get full path: `"+ioe.getMessage()+"'");
            return;
        }
        File target = new File(moddir, args[0]+".py");

        if (!target.canRead()) {
            sendError(sender, "Could not find command: `"+target+"'");
            return;
        }

        // Set up the command line appropriately.
        ArrayList<String> sargs = new ArrayList<String>();
        sargs.add("python");
        sargs.add(target.toString());
        for (int i=1; i<args.length; i++) {
            sargs.add(args[i]);
        }

        // Set up the process with a suitable working directory, and reset
        // stderr to stdio
        ProcessBuilder pb = new ProcessBuilder(sargs);
        pb.directory(moddir);
        pb.redirectErrorStream(true);
        Process p;
        try { p = pb.start(); }
        catch (IOException ioe) {
            sendError(sender, "Could not start python: "+ioe.getMessage());
            return;
        }
        // Drain its stdout/stderr stream.
        Thread drainer = new Drainer(p.getInputStream(), args[0]);
        drainer.setDaemon(true);
        drainer.start();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    { return true; }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    { return null; }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    { return false; }

    @Override
    public int compareTo(Object o)
    { return 0; }

    private final static void sendError(ICommandSender sender, String m)
    { sender.addChatMessage(new ChatComponentText(m)); }

    private final static Pattern s_safearg = Pattern.compile("[a-zA-Z0-9_]+");
    private final static List<String> s_aliases;
    static
    {
        s_aliases = new ArrayList<String>();
        s_aliases.add("py");
        s_aliases.add("python");
    }

    // used to absorb stdout/stderr in a separate thread.
    private final static class Drainer
        extends Thread
    {
        private Drainer(InputStream in, String id)
        {
            super("drain-"+id);
            m_in = in;
            m_id = id;
        }

        @Override
        public void run()
        {
            BufferedReader br = null;
            try {
                 br = new BufferedReader
                     (new InputStreamReader(m_in));
                 String line;
                 while ((line = br.readLine()) != null) {
                     System.out.println(m_id + ">" + line);
                 }
            }
            catch (Throwable th) {
                th.printStackTrace();
            }
            finally {
                if (br != null) {
                    try { br.close(); }
                    catch (Throwable ign) {}
                }
            }
        }
        private final InputStream m_in;
        private final String m_id;
    }
}
