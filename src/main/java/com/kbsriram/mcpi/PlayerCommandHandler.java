package com.kbsriram.mcpi;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class PlayerCommandHandler
{
    public final static class GetTile
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length != 1) || !("".equals(args[0]))) {
                return "Usage: player.getTile()";
            }
            EntityPlayerMP host = getHostPlayer(ws);
            if (host != null) {
                WorldInfo info = ws.getWorldInfo();
                return
                    String.valueOf(Util.asExtTileX(info, host))+","+
                    String.valueOf(Util.asExtTileY(info, host))+","+
                    String.valueOf(Util.asExtTileZ(info, host));
            }
            else {
                return "No host player?";
            }
        }
    }

    public final static class SetTile
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if (args.length != 3) {
                return "Usage: player.setTile(x,y,z)";
            }
            EntityPlayerMP host = getHostPlayer(ws);
            if (host == null) {
                return "No host player?";
            }
            WorldInfo info = ws.getWorldInfo();
            host.setPositionAndUpdate
                (Util.asX(info, args[0]),
                 Util.asY(info, args[1]),
                 Util.asZ(info, args[2]));
            return VOID;
        }
    }

    public final static class SetPos
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if (args.length != 3) {
                return "Usage: player.setPos(x,y,z)";
            }
            EntityPlayerMP host = getHostPlayer(ws);
            if (host == null) {
                return "No host player?";
            }
            WorldInfo info = ws.getWorldInfo();
            host.setPositionAndUpdate
                (Util.asDoubleX(info, args[0]),
                 Util.asDoubleY(info, args[1]),
                 Util.asDoubleZ(info, args[2]));
            return VOID;
        }
    }

    @SuppressWarnings("unchecked")
    private final static EntityPlayerMP getHostPlayer(WorldServer ws)
    {
        // Returning the first player - this is probably wrong.
        List<EntityPlayerMP> players = ws.playerEntities;
        if (players.size() == 0) { return null; }
        return players.get(0);
    }
}
