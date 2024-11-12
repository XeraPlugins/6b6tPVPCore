package me.ian.lobby.world;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author SevJ6
 */
public class VoidWorld extends ChunkGenerator {

    List<BlockPopulator> populators = new ArrayList<>();

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return populators;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        return getByteResult(new byte[world.getMaxHeight() / 16][]);
    }

    @Override
    public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        return getShortResult(new short[world.getMaxHeight() / 16][]);
    }

    private byte[][] getByteResult(byte[][] result) {
        return result;
    }

    private short[][] getShortResult(short[][] result) {
        return result;
    }
}
