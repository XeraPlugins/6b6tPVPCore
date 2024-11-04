package me.ian.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
}
