package me.ian.time.schedulers;

import com.moandjiezana.toml.Toml;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ian.PVPHelper;
import me.ian.time.ScheduledTask;
import me.ian.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

/**
 * @author SevJ6
 */
public class TabListUpdater {

    @ScheduledTask
    public static void updateTablist() {
        Toml toml = PVPHelper.INSTANCE.getRunningConfig().getToml().getTable("tablist");
        Bukkit.getOnlinePlayers().forEach(player -> {
            TextComponent componentHeader = new TextComponent(Utils.translateChars(PlaceholderAPI.setPlaceholders(player, toml.getString("header"))));
            TextComponent componentFooter = new TextComponent(Utils.translateChars(PlaceholderAPI.setPlaceholders(player, toml.getString("footer"))));
            player.setPlayerListHeaderFooter(componentHeader, componentFooter);
        });
    }
}
