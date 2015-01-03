package com.kbsriram.mcpi;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Simpleminded server - just opens up a port, and launches a client
// thread on each connected client.
//
// Each client thread parses an incoming mcpi request, and enqueues it
// into a blocking queue. It then blocks itself waiting for a response
// to the command.
//
// EngineTickHandler (which is woken up on every world tick) polls for
// commands in all clients, and processes one command per
// tick. Responses are then enqueued into a per-client return queue,
// as a simple string.
//
// The client thread now wakes up. It looks for a magic "void" string
// to signify nothing needs to be returned to the connected client;
// otherwise it prints the string back to the connected client.

public class CommandServer
    extends Thread
{
    public CommandServer(int port)
    {
        super("Mcpi Service thread");
        m_port = port;
    }

    final Command pollCommand()
    {
        synchronized(m_client_lock) {
            int len = m_clients.size();
            int cur = m_last_client_idx;
            for (int i=0; i<len; i++) {
                cur++;
                if (cur >= len) {
                    cur = 0;
                }
                Command ret = m_clients.get(cur).poll();
                if (ret != null) {
                    m_last_client_idx = cur;
                    return ret;
                }
            }
        }
        return null;
    }

    final void removeClient(ClientHandler ch)
    {
        synchronized(m_client_lock) {
            m_clients.remove(ch);
        }
    }

    final boolean isRunning()
    { return m_running; }

    @Override
    public void run()
    {
        try { _run(); }
        catch (Throwable th) {
            s_logger.warn("Server died!", th);
            m_running = false;
        }
    }

    private final void _run()
        throws Exception
    {
        s_logger.info("Starting command server at "+m_port);

        // Simpleminded implementation - only accepts one client at a
        // time.
        ServerSocket ss = new ServerSocket(m_port, 1);
        while (true) {
            Socket cli = ss.accept();
            s_logger.info("New mcpi client connected: "+cli);
            ClientHandler ch = new ClientHandler
                ("Mcpi Client Thread", this, cli);
            ch.setDaemon(true);
            synchronized(m_client_lock) {
                m_clients.add(ch);
            }
            ch.start();
        }
    }

    private final int m_port;
    private final Object m_client_lock = new Object();
    private final ArrayList<ClientHandler> m_clients =
        new ArrayList<ClientHandler>();
    private int m_last_client_idx = 0;
    private boolean m_running = true;
    private final static Logger s_logger = LogManager.getLogger();
}
