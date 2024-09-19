package me.ian;

import lombok.Getter;
import me.ian.command.CommandRegistrar;
import me.ian.io.Config;
import me.ian.tab.TabListUpdater;
import me.ian.time.TaskRegistrar;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author SevJ6
 */
public class PVPHelper extends JavaPlugin {

    public static final long START_TIME = System.currentTimeMillis();
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(3);
    public static PVPHelper INSTANCE;
    private Config config;

    @Getter
    private CommandRegistrar commandRegistrar;

    // Return the custom Toml configuration
    public Config getRunningConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        config = new Config("config.toml");
        commandRegistrar = new CommandRegistrar();
        commandRegistrar.registerAllCommands();
        TaskRegistrar.register(TabListUpdater.class);
    }

    @Override
    public void onDisable() {

    }

}