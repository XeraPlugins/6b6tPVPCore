package me.ian;

import me.ian.io.Config;
import org.bukkit.plugin.java.JavaPlugin;

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
        config.printConfig(); // debug
    }

    @Override
    public void onDisable() {

    }

}