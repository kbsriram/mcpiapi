package com.kbsriram.mcpi;

import net.minecraft.world.WorldServer;

public interface ICommandHandler
{
    // Magic value meaning, don't return anything to the client.
    public final static String VOID = "-*void*-";
    // Magic value meaning, immediately disconnect the client.
    public final static String KILL = "-*kill*-";
    // Magic value meaning, the handler wants you to pause for
    // the next response
    public final static String PAUSE = "-*pause*-";

    public String handle(Command cmd, WorldServer ws)
        throws Exception;
}
