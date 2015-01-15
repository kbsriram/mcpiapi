package com.kbsriram.mcpi;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class EventCommandHandler
{
    public final static class Clear
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length != 1) || !("".equals(args[0]))) {
                return "Usage: events.clear()";
            }
            BlockEventHandler.set(null);
            return VOID;
        }
    }

    // Send a response when any player moves to a new tile
    public final static class PlayerWaitMovedTile
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length != 1) || !("".equals(args[0]))) {
                return "Usage: events.player.wait.movedTile()";
            }
            // Stash the client handler so the block event handler
            // can send the response eventually.
            if (!PlayerEventHandler.setPendingCommand(ws, cmd)) {
                return "No players available.";
            }
            // Ask the return queue processor to pause for the next
            // message.
            return ICommandHandler.PAUSE;
        }
    }

    // Send a response the first time any block is hit.
    public final static class BlockWaitHit
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length != 1) || !("".equals(args[0]))) {
                return "Usage: events.block.wait.hit()";
            }
            // Stash the client handler so the block event handler
            // can send the response eventually.
            BlockEventHandler.set(null);
            BlockEventHandler.setPendingCommand(cmd);
            // Ask the return queue processor to pause for the next
            // message.
            return ICommandHandler.PAUSE;
        }
    }

    // Return all the block hits since the last time we were called.
    public final static class BlockHits
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length != 1) || !("".equals(args[0]))) {
                return "Usage: events.block.hits()";
            }
            HitData data = BlockEventHandler.get();
            String ret = (data != null) ? data.asString(ws.getWorldInfo()) : "";
            BlockEventHandler.set(null);
            return ret;
        }
    }

    public final static class BlockEventHandler
    {
        @SubscribeEvent
        public void onPlayerInteractEvent(PlayerInteractEvent e)
        {
            if (e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                HitData hd = new HitData
                    (e.x, e.y, e.z, e.face, e.entityPlayer.getEntityId());
                BlockEventHandler.set(hd);

                if (s_pending_command != null) {
                    // Enqueue a response.
                    try {
                        WorldServer ws = EngineTickHandler.getWorldServer();
                        if (ws != null) {
                            s_pending_command.setReturn
                                (hd.asString(ws.getWorldInfo()));
                        }
                    }
                    finally {
                        s_pending_command = null;
                    }
                }
            }

            if (WorldCommandHandler.Setting.s_immutable) {
                e.setCanceled(true);
            }
        }

        final static void set(HitData hd)
        { s_last_block_hit = hd; }
        final static void setPendingCommand(Command c)
        { s_pending_command = c; }
        final static HitData get()
        { return s_last_block_hit; }

        private static HitData s_last_block_hit = null;
        private static Command s_pending_command = null;
    }

    public final static class PlayerEventHandler
    {
        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent e)
        {
            if (s_pending_command == null) { return; }
            if (e.side != Side.SERVER) { return; }

            int id = e.player.getEntityId();
            Track orig = null;
            for (int i=s_tracked.length-1; i>=0; i--) {
                Track cur = s_tracked[i];
                if (cur.m_id == id) {
                    orig = cur;
                    break;
                }
            }
            if (orig == null) {
                return;
            }
            int nx = MathHelper.floor_double(e.player.posX);
            int ny = MathHelper.floor_double(e.player.posY);
            int nz = MathHelper.floor_double(e.player.posZ);
            if ((orig.m_x != nx) || (orig.m_y != ny) || (orig.m_z != nz)) {
                try {
                    WorldServer ws = EngineTickHandler.getWorldServer();
                    WorldInfo info = ws.getWorldInfo();
                    if (ws != null) {
                        s_pending_command.setReturn
                            (id+","+(nx - info.getSpawnX())+","+
                             (ny - info.getSpawnY())+","+
                             (nz - info.getSpawnZ()));
                    }
                }
                finally {
                    s_pending_command = null;
                    s_tracked = null;
                }
            }
        }

        final static boolean setPendingCommand(WorldServer ws, Command c)
        {
            List<EntityPlayerMP> players = ws.playerEntities;
            int plen = players.size();
            if (plen == 0) { return false; }
            s_tracked = new Track[plen];
            for (int i=0; i<plen; i++) {
                s_tracked[i] = Track.createFrom(players.get(i));
            }
            s_pending_command = c;
            return true;
        }

        private static Command s_pending_command = null;
        private static Track[] s_tracked = null;
    }

    private final static class Track
    {
        private final static Track createFrom(EntityPlayerMP player)
        {
            return new Track
                (player.getEntityId(),
                 MathHelper.floor_double(player.posX),
                 MathHelper.floor_double(player.posY),
                 MathHelper.floor_double(player.posZ));
        }
        private Track(int id, int x, int y, int z)
        {
            m_id = id;
            m_x = x;
            m_y = y;
            m_z = z;
        }
        private final int m_id;
        private final int m_x;
        private final int m_y;
        private final int m_z;
    }

    private final static class HitData
    {
        private HitData(int x, int y, int z, int face, int eid)
        {
            m_x = x;
            m_y = y;
            m_z = z;
            m_face = face;
            m_eid = eid;
        }
        private String asString(WorldInfo info)
        {
            return
                (m_x - info.getSpawnX())+","+
                (m_y - info.getSpawnY())+","+
                (m_z - info.getSpawnZ())+","+
                m_face+","+m_eid;
        }
        private final int m_x;
        private final int m_y;
        private final int m_z;
        private final int m_face;
        private final int m_eid;
    }
}
