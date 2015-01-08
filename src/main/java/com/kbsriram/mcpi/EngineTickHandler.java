package com.kbsriram.mcpi;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EngineTickHandler
{
    public EngineTickHandler(CommandServer cs)
    { m_cs = cs; }

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent e)
    {
        // sanity checks.
        if ((m_cs == null) ||
            (e.side != Side.SERVER) ||
            (e.phase != Phase.END)) {
            return;
        }
        final MinecraftServer ms = MinecraftServer.getServer();
        if (ms == null) {
            return;
        }
        final WorldServer ws[] = ms.worldServers;
        if ((ws.length == 0) || ws[0].isRemote) {
            return;
        }

        if (!m_cs.isRunning()) {
            m_cs = null;
            return;
        }

        Command cmd = m_cs.pollCommand();
        if (cmd == null) { return; }

        ICommandHandler handler = s_handlers.get(cmd.getName());
        if (handler == null) {
            resp(cmd, "Unknown command: `"+cmd.getName()+"'");
            return;
        }

        try { resp(cmd, handler.handle(cmd, ws[0])); }
        catch (Exception ex) {
            s_logger.warn("Handler failed.", ex);
            resp(cmd, ex.getMessage());
        }
    }

    private void resp(Command cmd, String s)
    { cmd.setReturn(s); }

    private CommandServer m_cs;
    private final static Logger s_logger = LogManager.getLogger();
    private final static Map<String,ICommandHandler> s_handlers =
        new HashMap<String,ICommandHandler>();
    static
    {
        s_handlers.put("world.setBlock", new WorldCommandHandler.SetBlock());
        s_handlers.put("world.setBlocks", new WorldCommandHandler.SetBlocks());
        s_handlers.put("world.getHeight", new WorldCommandHandler.GetHeight());
        s_handlers.put("world.getBlock", new WorldCommandHandler.GetBlock());
        s_handlers.put("world.getPlayerIds", new WorldCommandHandler.GetPlayerIds());
        s_handlers.put("world.setting", new WorldCommandHandler.Setting());

        s_handlers.put("chat.post", new ChatCommandHandler.Post());

        // aliases so we can pass in either a name or an entity id.
        s_handlers.put("player.getTile", new PlayerCommandHandler.GetTile(false));
        s_handlers.put("player.setTile", new PlayerCommandHandler.SetTile(false));
        s_handlers.put("player.setPos", new PlayerCommandHandler.SetPos(false));
        s_handlers.put("player.getPos", new PlayerCommandHandler.GetPos(false));
        s_handlers.put("player.getRotationYaw", new PlayerCommandHandler.GetRotationYaw(false));

        s_handlers.put("entity.getTile", new PlayerCommandHandler.GetTile(true));
        s_handlers.put("entity.setTile", new PlayerCommandHandler.SetTile(true));
        s_handlers.put("entity.setPos", new PlayerCommandHandler.SetPos(true));
        s_handlers.put("entity.getPos", new PlayerCommandHandler.GetPos(true));
        s_handlers.put("entity.getRotationYaw", new PlayerCommandHandler.GetRotationYaw(true));

        s_handlers.put("events.clear", new EventCommandHandler.Clear());
        s_handlers.put("events.block.hits", new EventCommandHandler.BlockHits());

        s_handlers.put("camera.mode.setNormal", new CameraCommandHandler.ModeSetNormal());
        s_handlers.put("camera.mode.setFollow", new CameraCommandHandler.ModeSetFollow());
        s_handlers.put("camera.mode.setFixed", new CameraCommandHandler.ModeSetFixed());
        s_handlers.put("camera.setPos", new CameraCommandHandler.SetPos());
    }
}
