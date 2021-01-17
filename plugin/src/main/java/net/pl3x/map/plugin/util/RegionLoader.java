package net.pl3x.map.plugin.util;

import net.minecraft.server.v1_16_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.WorldServer;

import java.io.IOException;

public class RegionLoader {
    public NBTTagCompound getRegionNBT(WorldServer world, int x, int z) {
        ChunkCoordIntPair pair = new ChunkCoordIntPair(x, z);
        try {
            return world.getChunkProvider().playerChunkMap.read(pair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
