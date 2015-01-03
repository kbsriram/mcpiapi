package com.kbsriram.mcpi;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.WorldInfo;

public class Util
{
    public final static int asExtTileX(WorldInfo info, Entity e)
    { return MathHelper.floor_double(e.posX) - info.getSpawnX(); }

    public final static int asExtTileY(WorldInfo info, Entity e)
    { return MathHelper.floor_double(e.posY) - info.getSpawnY(); }

    public final static int asExtTileZ(WorldInfo info, Entity e)
    { return MathHelper.floor_double(e.posZ) - info.getSpawnZ(); }

    public final static int asX(WorldInfo info, String v)
    { return info.getSpawnX() + Integer.parseInt(v); }

    public final static int asY(WorldInfo info, String v)
    { return info.getSpawnY() + Integer.parseInt(v); }

    public final static int asZ(WorldInfo info, String v)
    { return info.getSpawnZ() + Integer.parseInt(v); }

    public final static double asDoubleX(WorldInfo info, String v)
    { return info.getSpawnX() + Double.parseDouble(v); }

    public final static double asDoubleY(WorldInfo info, String v)
    { return info.getSpawnY() + Double.parseDouble(v); }

    public final static double asDoubleZ(WorldInfo info, String v)
    { return info.getSpawnZ() + Double.parseDouble(v); }
}
