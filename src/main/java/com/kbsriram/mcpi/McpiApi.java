package com.kbsriram.mcpi;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = McpiApi.MODID, version = McpiApi.VERSION)
public class McpiApi
{
    public static final String MODID = "mcpi";
    public static final String VERSION = "1.0";
    private static final int API_PORT = 4711;

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        // This exposes a command for the player to run python commands
        // externally. Intended to be used to run mcpi client apps
        // more conveniently.
        event.registerServerCommand(new ExternalPythonCommand());
    }

    // This strange initialization hook is because it seems that the
    // server mod isn't being called in SSP mode.
    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
            return;
        }

        // create server thread.
        m_cs = new CommandServer(API_PORT);

        // EngineTickHandler runs on every world tick, and
        // looks for new commands to process.
        FMLCommonHandler.instance().bus().register
            (new EngineTickHandler(m_cs));

        // PlayerTickHandler runs on every player tick, and
        // handles any pending player move events.
        FMLCommonHandler.instance().bus().register
            (new EventCommandHandler.PlayerEventHandler());


        // EventCommandHandler.BlockHandler is called on various
        // block events. Used to save data for event.* commands
        MinecraftForge.EVENT_BUS.register
            (new EventCommandHandler.BlockEventHandler());


        // Start up the server thread.
        m_cs.start();
    }
    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event)
    {
        if (m_cs != null) {
            try { m_cs.stopServer(); }
            finally {
                m_cs = null;
            }
        }
    }

    private CommandServer m_cs = null;
}
