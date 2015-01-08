package com.kbsriram.mcpi;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class PlayerCommandHandler
{
    public static abstract class ABaseCommandHandler
        implements ICommandHandler
    {
        ABaseCommandHandler(boolean idIsNumber)
        { m_idIsNumber = idIsNumber; }

        @SuppressWarnings("unchecked")
        protected final EntityPlayerMP getPlayer(WorldServer ws, String name)
        {
            List<EntityPlayerMP> players = ws.playerEntities;
            if (players.size() == 0) { return null; }

            if (m_idIsNumber) {
                // Using numeric id to pull out the player.
                int id;
                try { id = Integer.valueOf(name); }
                catch(NumberFormatException nfe) {
                    return null;
                }
                for (int i=players.size() - 1; i >= 0; i--) {
                    EntityPlayerMP cur = players.get(i);
                    if (id == cur.getEntityId()) {
                        return cur;
                    }
                }
                return null;
            }
            // "Name" technique.
            // Returning the first player if none explicitly provided.
            // Python clients tend to have 'None' sent in place of an
            // unspecified name, so treat that specially as well.
            if ((name == null) || ("".equals(name)) || ("None".equals(name))) {
                return players.get(0);
            }
            for (int i=players.size() - 1; i >= 0; i--) {
                EntityPlayerMP cur = players.get(i);
                if (name.equals(cur.getCommandSenderName())) {
                    return cur;
                }
            }
            return null;
        }
        private final boolean m_idIsNumber;
    }

    public final static class GetTile
        extends ABaseCommandHandler
    {
        public GetTile(boolean v)
        { super(v); }

        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if (args.length != 1) {
                return "Usage: player.getTile([playername])";
            }
            EntityPlayerMP host = getPlayer(ws, args[0]);
            if (host != null) {
                WorldInfo info = ws.getWorldInfo();
                return
                    String.valueOf(Util.asExtTileX(info, host))+","+
                    String.valueOf(Util.asExtTileY(info, host))+","+
                    String.valueOf(Util.asExtTileZ(info, host));
            }
            else {
                return "No such player";
            }
        }
    }

    public final static class SetTile
        extends ABaseCommandHandler
    {
        public SetTile(boolean v)
        { super(v); }

        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length < 3) || (args.length > 4)) {
                return "Usage: player.setTile([playername,]x,y,z)";
            }
            String player = (args.length == 3) ? null: args[0];
            EntityPlayerMP host = getPlayer(ws, player);
            if (host == null) {
                return "No such player";
            }
            WorldInfo info = ws.getWorldInfo();
            int idx = (args.length == 3) ? 0: 1;
            host.setPositionAndUpdate
                (Util.asX(info, args[idx]),
                 Util.asY(info, args[idx+1]),
                 Util.asZ(info, args[idx+2]));
            return VOID;
        }
    }

    public final static class GetRotationYaw
        extends ABaseCommandHandler
    {
        public GetRotationYaw(boolean v)
        { super(v); }

        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if (args.length != 1) {
                return "Usage: player.getRotationYaw([playername])";
            }
            EntityPlayerMP host = getPlayer(ws, args[0]);
            if (host != null) {
                WorldInfo info = ws.getWorldInfo();
                return String.valueOf(host.rotationYaw);
            }
            else {
                return "No such player";
            }
        }
    }

    public final static class GetPos
        extends ABaseCommandHandler
    {
        public GetPos(boolean v)
        { super(v); }

        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if (args.length != 1) {
                return "Usage: player.getPos([playername])";
            }
            EntityPlayerMP host = getPlayer(ws, args[0]);
            if (host != null) {
                WorldInfo info = ws.getWorldInfo();
                return
                    String.valueOf(Util.asExtTileXPos(info, host))+","+
                    String.valueOf(Util.asExtTileYPos(info, host))+","+
                    String.valueOf(Util.asExtTileZPos(info, host));
            }
            else {
                return "No such player";
            }
        }
    }

    public final static class SetPos
        extends ABaseCommandHandler
    {
        public SetPos(boolean v)
        { super(v); }

        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length < 3) || (args.length > 4)) {
                return "Usage: player.setPos([playername,]x,y,z)";
            }
            String player = (args.length == 3) ? null: args[0];
            EntityPlayerMP host = getPlayer(ws, player);
            if (host == null) {
                return "No such player";
            }
            WorldInfo info = ws.getWorldInfo();
            int idx = (args.length == 3) ? 0: 1;
            host.setPositionAndUpdate
                (Util.asDoubleX(info, args[idx]),
                 Util.asDoubleY(info, args[idx+1]),
                 Util.asDoubleZ(info, args[idx+2]));
            return VOID;
        }
    }

}
