package me.ian.utils;

import net.minecraft.server.v1_12_R1.ChunkProviderGenerate;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.StructureBoundingBox;
import net.minecraft.server.v1_12_R1.StructureStart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @author SevJ6
 */
public class Utils {

    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(formatter);
    }

    public static void sendMessage(Object recipient, String message) {
        message = translateChars(message);
        if (recipient instanceof Player) ((Player) recipient).sendMessage(message);
        else if (recipient instanceof CommandSender) ((CommandSender) recipient).sendMessage(message);
    }

    public static String translateChars(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String getFormattedInterval(long ms) {
        long seconds = ms / 1000L % 60L;
        long minutes = ms / 60000L % 60L;
        long hours = ms / 3600000L % 24L;
        long days = ms / 86400000L;
        return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }

    public static ChatColor getTPSColor(double tps) {
        if (tps >= 18.0D) {
            return ChatColor.GREEN;
        } else {
            return tps >= 13.0D ? ChatColor.YELLOW : ChatColor.RED;
        }
    }
    public static void invokeMethodUnderBukkit(Method method, JavaPlugin plugin) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                method.invoke(null);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static EntityPlayer getHandle(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public static String translateColorCodes(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static void generateVillage(World world, Random random, Chunk chunk) {
        try {
            int radius = random.nextInt(50);
            CraftWorld w = (CraftWorld) world;
            ChunkProviderGenerate chunkProvider = (ChunkProviderGenerate) w.getHandle().worldProvider.getChunkGenerator();
            Field randomField = ChunkProviderGenerate.class.getDeclaredField("i");
            randomField.setAccessible(true);
            Random rand = (Random) randomField.get(chunkProvider);
            Class<?> clazz = Class.forName("net.minecraft.server.v1_12_R1.WorldGenVillage$WorldGenVillageStart");
            Constructor<?> constructor = clazz.getConstructor(net.minecraft.server.v1_12_R1.World.class, Random.class, int.class, int.class, int.class);
            StructureStart structureStart = (StructureStart) constructor.newInstance(w.getHandle(), rand, chunk.getX(), chunk.getZ(), 0);
            int centerX = (chunk.getX() << 4) + 8;
            int centerZ = (chunk.getZ() << 4) + 8;
            structureStart.a(w.getHandle(), rand, new StructureBoundingBox(centerX - radius, centerZ - radius, centerX + radius, centerZ + radius));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
