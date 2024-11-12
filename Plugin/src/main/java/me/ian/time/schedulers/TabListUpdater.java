package me.ian.time.schedulers;

import com.moandjiezana.toml.Toml;
import me.ian.PVPHelper;
import me.ian.time.ScheduledTask;
import me.ian.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author SevJ6
 */
public class TabListUpdater {

    @ScheduledTask
    public static void updateTablist() {
        Toml toml = PVPHelper.INSTANCE.getRunningConfig().getToml().getTable("tablist");
        Bukkit.getOnlinePlayers().forEach(player -> {
            TextComponent componentHeader = new TextComponent(parsePlaceHolders(toml.getString("header"), player));
            TextComponent componentFooter = new TextComponent(parsePlaceHolders(toml.getString("footer"), player));
            player.setPlayerListHeaderFooter(componentHeader, componentFooter);
        });
    }

    private static String parsePlaceHolders(String input, Player player) {
        double tps = ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
        String strTps = (tps >= 20) ? String.format("%s*20.0", ChatColor.GREEN) : String.format("%s%.2f", Utils.getTPSColor(tps), tps);
        String uptime = Utils.getFormattedInterval(System.currentTimeMillis() - PVPHelper.START_TIME);
        String online = String.valueOf(Bukkit.getOnlinePlayers().size());
        int rawPing = getPing(player);
        String ping = rawPing >= 250 ? ChatColor.RED + String.valueOf(rawPing) : rawPing >= 150 ? ChatColor.YELLOW + String.valueOf(rawPing) : ChatColor.GREEN + String.valueOf(rawPing);
        return Utils.translateChars(input.replace("%tps%", strTps).replace("%players%", online)).replace("%ping%", ping).replace("%uptime%", uptime);
    }

    private static int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }
}
