package com.kbsriram.mcpi;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.server.MinecraftServer;
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
                BlockEventHandler.set
                    (new HitData
                     (e.x, e.y, e.z, e.face, e.entityPlayer.getEntityId()));
            }
            if (WorldCommandHandler.Setting.s_immutable) {
                e.setCanceled(true);
            }
        }

        final static void set(HitData hd)
        { s_last_block_hit = hd; }
        final static HitData get()
        { return s_last_block_hit; }

        private static HitData s_last_block_hit = null;
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
