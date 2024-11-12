package me.ian;

import lombok.Getter;
import me.ian.command.CommandRegister;
import me.ian.event.EventRegister;
import me.ian.lobby.npc.NPCManager;
import me.ian.lobby.world.VoidWorld;
import me.ian.time.TaskRegister;
import me.ian.time.schedulers.TabListUpdater;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
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
    private CommandRegister commandRegister;

    @Getter
    private EventRegister eventRegister;

    @Getter
    private ScheduledExecutorService executorService;

    @Getter
    private NPCManager npcManager;

    // Return the custom Toml configuration
    public Config getRunningConfig() {
        return config;
    }

    // custom world generation to generate nothing but air blocks for Multiverse-Core
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidWorld();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        loadMixins();
        config = new Config("config.toml");
        executorService = Executors.newScheduledThreadPool(4);
        commandRegister = new CommandRegister();
        commandRegister.registerCommands();
        eventRegister = new EventRegister();
        eventRegister.registerEvents();
        npcManager = new NPCManager();
        TaskRegister.register(TabListUpdater.class);
    }

    @Override
    public void onDisable() {

    }

    private void loadMixins() {
        File mixinJar = new File(".", "mixins-temp.jar");
        if (mixinJar.exists()) mixinJar.delete();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("mixins.dat");
            if (is == null) throw new RuntimeException("The plugin jar is missing the mixins");
            Files.copy(is, mixinJar.toPath());
            URLClassLoader ccl = new URLClassLoader(new URL[]{mixinJar.toURI().toURL()});
            Class<?> mixinMainClass = Class.forName(String.format("%s.mixin.MixinMain", getClass().getPackage().getName()), true, ccl);
            Object instance = mixinMainClass.newInstance();
            Method mainM = instance.getClass().getDeclaredMethod("init", JavaPlugin.class);
            mainM.invoke(instance, this);
        } catch (Throwable t) {
            getLogger().severe(String.format("Failed to load mixins due to %s. Please see the stacktrace below for more info", t.getClass().getName()));
            t.printStackTrace();
        } finally {
            if (mixinJar.exists()) mixinJar.delete();
        }
    }
}