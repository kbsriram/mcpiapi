package com.kbsriram.mcpi;

import net.minecraft.world.WorldServer;

public interface ICommandHandler
{
    // Magic value meaning, don't return anything to the client.
    public final static String VOID = "-*void*-";
    // Magic value meaning, immediately disconnect the client.
    public final static String KILL = "-*kill*-";

    public String handle(Command cmd, WorldServer ws)
        throws Exception;
}
