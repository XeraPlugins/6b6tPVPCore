package me.ian.lobby.world;

import net.minecraft.server.v1_12_R1.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class VoidChunkGenerator implements ChunkGenerator {

    private final World world;

    public VoidChunkGenerator(World world) {
        this.world = world;
    }

    @Override
    public Chunk getOrCreateChunk(int i, int i1) {
        return new Chunk(world, i, i1);
    }

    @Override
    public void recreateStructures(int i, int i1) {

    }

    @Override
    public boolean a(Chunk chunk, int i, int i1) {
        return false;
    }

    @Override
    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumCreatureType, BlockPosition blockPosition) {
        BiomeBase biomebase = this.world.getBiome(blockPosition);
        return biomebase.getMobs(enumCreatureType);
    }

    @Nullable
    @Override
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockPosition, boolean b) {
        return new BlockPosition(0, 0, 0);
    }

    @Override
    public void recreateStructures(Chunk chunk, int i, int i1) {

    }

    @Override
    public boolean a(World world, String s, BlockPosition blockPosition) {
        return false;
    }
}
