package me.ian.lobby.world;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.generator.InternalChunkGenerator;

import java.util.Collections;
import java.util.List;

public class VoidGen extends InternalChunkGenerator {

    private final ChunkGenerator generator;

    public VoidGen(World world) {
        this.generator = new VoidChunkGenerator(world);
    }

    public Chunk getOrCreateChunk(int i, int i1) {
        return this.generator.getOrCreateChunk(i, i1);
    }

    public void recreateStructures(int i, int i1) {

    }

    public boolean a(Chunk chunk, int i, int i1) {
        return this.generator.a(chunk, i, i1);
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumCreatureType, BlockPosition blockPosition) {
        return Collections.emptyList();
    }

    public BlockPosition findNearestMapFeature(net.minecraft.server.v1_12_R1.World world, String s, BlockPosition blockPosition, boolean flag) {
        return new BlockPosition(0, 0, 0);
    }

    public void recreateStructures(Chunk chunk, int i, int i1) {

    }

    public boolean a(net.minecraft.server.v1_12_R1.World world, String string, BlockPosition bp) {
        return false;
    }
}
