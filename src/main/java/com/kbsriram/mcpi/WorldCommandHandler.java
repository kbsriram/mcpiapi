package com.kbsriram.mcpi;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class WorldCommandHandler
{
    public final static class GetBlock
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            if (cmd.getArgs().length != 3) {
                return "usage: world.getBlock(x,y,z)";
            }
            WorldInfo info = ws.getWorldInfo();

            String[] args = cmd.getArgs();
            int x = Util.asX(info, args[0]);
            int y = Util.asY(info, args[1]);
            int z = Util.asZ(info, args[2]);
            Block b = ws.getBlock(x, y, z);
            return String.valueOf(Block.getIdFromBlock(b));
        }
    }

    public final static class GetHeight
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)

            throws Exception
        {
            if (cmd.getArgs().length != 2) {
                return "usage: world.getHeight(x,z)";
            }
            WorldInfo info = ws.getWorldInfo();

            String[] args = cmd.getArgs();
            int x = Util.asX(info, args[0]);
            int z = Util.asZ(info, args[1]);
            return String.valueOf(ws.getHeightValue(x, z) - info.getSpawnY());
        }
    }

    public final static class SetBlock
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length < 4) || (args.length > 6)) {
                return "usage: world.setBlock(x,y,z,blockid,[blockmetadata,flag])";
            }
            WorldInfo info = ws.getWorldInfo();
            int x = Util.asX(info, args[0]);
            int y = Util.asY(info, args[1]);
            int z = Util.asZ(info, args[2]);
            int bid = Integer.parseInt(args[3]);
            int meta;
            if (args.length >= 5) {
                meta = Integer.parseInt(args[4]);
            }
            else {
                meta = 0;
            }
            int flags;
            if (args.length == 6) {
                flags = Integer.parseInt(args[5]);
            }
            else {
                flags = 3;
            }
            ws.setBlock(x, y, z, Block.getBlockById(bid), meta, flags);
            return VOID;
        }
    }

    public final static class SetTileEntityHex
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if (args.length != 4) {
                return "usage: world.setTileEntityHex(x,y,z,NBThex)";
            }
            WorldInfo info = ws.getWorldInfo();
            int x = Util.asX(info, args[0]);
            int y = Util.asY(info, args[1]);
            int z = Util.asZ(info, args[2]);
            byte hex[] = Util.hexToBytes(args[3]);
            NBTTagCompound tag = CompressedStreamTools.readCompressed
                (new ByteArrayInputStream(hex));
            TileEntity te = TileEntity.createAndLoadEntity(tag);
            if (te == null) {
                return "no such tileentity available.";
            }
            ws.setTileEntity(x, y, z, te);
            te.updateContainingBlockInfo();
            ws.markBlockForUpdate(x, y, z);
            return VOID;
        }
    }

    public final static class SetBlocks
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            String[] args = cmd.getArgs();
            if ((args.length < 7) || (args.length > 9)) {
                return "usage: world.setBlocks(x1,y1,z1,x2,y2,z2,blockid[,meta,flag])";
            }
            int meta;
            if (args.length >= 8) {
                meta = Integer.parseInt(args[7]);
            }
            else {
                meta = 0;
            }
            int flags;
            if (args.length == 9) {
                flags = Integer.parseInt(args[8]);
            }
            else {
                flags = 3;
            }

            WorldInfo info = ws.getWorldInfo();

            int x1 = Util.asX(info, args[0]);
            int y1 = Util.asY(info, args[1]);
            int z1 = Util.asZ(info, args[2]);
            int x2 = Util.asX(info, args[3]);
            int y2 = Util.asY(info, args[4]);
            int z2 = Util.asZ(info, args[5]);
            int bid = Integer.parseInt(args[6]);
            if (x1 > x2) { int tmp = x1; x1 = x2; x2 = tmp; }
            if (y1 > y2) { int tmp = y1; y1 = y2; y2 = tmp; }
            if (y1 < 0) { y1 = 0; }
            if (y2 > 255) { y2 = 255; }
            if (z1 > z2) { int tmp = z1; z1 = z2; z2 = tmp; }
            Block b = Block.getBlockById(bid);
            for (int x = x1; x<= x2; x++) {
                for (int z=z1; z<=z2; z++) {
                    for (int y=y1; y<=y2; y++) {
                        ws.setBlock(x, y, z, b, meta, flags);
                    }
                }
            }
            return VOID;
        }
    }

    public final static class GetPlayerIds
        implements ICommandHandler
    {
        @SuppressWarnings("unchecked")
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            if ((cmd.getArgs().length != 1) ||
                !("".equals(cmd.getArgs()[0]))) {
                return "usage: world.getPlayerIds()";
            }
            StringBuilder sb = new StringBuilder();
            List<EntityPlayer> players = (List<EntityPlayer>) ws.playerEntities;
            boolean first = true;
            for (EntityPlayer player: players) {
                if (first) { first = false; }
                else { sb.append("|"); }
                sb.append(String.valueOf(player.getEntityId()));
            }
            return sb.toString();
        }
    }

    public final static class Setting
        implements ICommandHandler
    {
        public String handle(Command cmd, WorldServer ws)
            throws Exception
        {
            if (cmd.getArgs().length != 2) {
                return "usage: world.setting(key,value)";
            }
            String key = cmd.getArgs()[0].trim();
            String value = cmd.getArgs()[1].trim();

            if (KEY_IMMUTABLE.equals(key)) {
                s_immutable = "1".equals(value);
            }
            else if (KEY_TIME.equals(key)) {
                // set the time in the world to the provided number.
                long time = Long.parseLong(value);
                ws.setWorldTime(time);
            }
            return VOID;
        }
        public static boolean s_immutable = false;
        private final static String KEY_IMMUTABLE = "immutable";
        private final static String KEY_TIME = "time";
    }
}
