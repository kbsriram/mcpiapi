package com.kbsriram.mcpi;

final class Command
{
    Command(ClientHandler ch, String name, String[] args)
    {
        m_ch = ch;
        m_name = name;
        m_args = args;
    }
    final String getName()
    { return m_name; }
    final String[] getArgs()
    { return m_args; }
    public String toString()
    { return m_name+"(...)"; }
    final void setReturn(String ret)
    { m_ch.enqueueReturn(ret); }

    private final String m_name;
    private final String[] m_args;
    private final ClientHandler m_ch;
}
