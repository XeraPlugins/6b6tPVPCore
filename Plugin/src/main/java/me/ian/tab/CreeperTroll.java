package me.ian.tab;

import me.ian.time.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.concurrent.ThreadLocalRandom;

public class CreeperTroll {

    @ScheduledTask(delay = 50L)
    public static void spawnCreepers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Location location = player.getLocation();
            int radius = 30;

            location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX() + genRandom(radius), location.getY() + genRandom(radius), location.getZ() + genRandom(radius)), EntityType.CREEPER);
        });
    }

    private static int genRandom(int radius) {
        return ThreadLocalRandom.current().nextInt(-radius, radius);
    }
}
