package me.ian;

import com.moandjiezana.toml.Toml;
import me.ian.io.Config;
import me.ian.tab.TabList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * @author SevJ6
 */
public class PVPHelper extends JavaPlugin {

    public static PVPHelper INSTANCE;
    private Config config;

    // Return the custom Toml configuration
    public Config getRunningConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        config = new Config("config.toml");

        getCommand("test").setExecutor(((sender, command, label, args) -> {
            TabList tabList = config.getToml().getTable("tablist").to(TabList.class);
            System.out.println(tabList.getHeader());
            System.out.println(tabList.getFooter());
            return true;
        }));
    }

    @Override
    public void onDisable() {

    }

}