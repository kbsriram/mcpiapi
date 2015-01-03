package com.kbsriram.mcpi;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
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

    // This strange initialization hook is because it seems that the
    // server mod isn't being called in SSP mode.
    @EventHandler
    public void onServerLoaded(FMLServerStartedEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
            return;
        }

        // create server thread.
        CommandServer cs = new CommandServer(API_PORT);

        // EngineTickHandler runs on every world tick, and
        // looks for new commands to process.
        FMLCommonHandler.instance().bus().register
            (new EngineTickHandler(cs));

        // EventCommandHandler.BlockHandler is called on various
        // block events. Used to save data for event.* commands
        MinecraftForge.EVENT_BUS.register
            (new EventCommandHandler.BlockEventHandler());

        // Start up the server thread.
        cs.start();
    }
}
