package me.ian;

import co.aikar.timings.TimedChunkGenerator;
import lombok.Getter;
import me.ian.arena.ArenaManager;
import me.ian.command.CommandManager;
import me.ian.duels.DuelManager;
import me.ian.general.EventManager;
import me.ian.kits.KitManager;
import me.ian.lobby.npc.NPCManager;
import me.ian.lobby.world.VoidGen;
import me.ian.portal.PortalManager;
import me.ian.time.TaskManager;
import me.ian.time.schedulers.TabListUpdater;
import me.ian.utils.area.BoundingBoxManager;
import me.txmc.protocolapi.PacketEventDispatcher;
import me.txmc.protocolapi.PacketListener;
import me.txmc.protocolapi.reflection.ClassProcessor;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author SevJ6
 */
public class PVPHelper extends JavaPlugin {

    public static final long START_TIME = System.currentTimeMillis();
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(4);
    public static PVPHelper INSTANCE;
    private Config config;
    @Getter
    private PacketEventDispatcher dispatcher;

    private List<ViolationManager> violationManagers;

    @Getter
    private DuelManager duelManager;

    @Getter
    private ArenaManager arenaManager;

    @Getter
    private CommandManager commandManager;

    @Getter
    private EventManager eventRegister;

    @Getter
    private NPCManager npcManager;

    @Getter
    private KitManager kitManager;

    @Getter
    private BoundingBoxManager boundingBoxManager;

    @Getter
    private PortalManager portalManager;

    // Return the custom Toml configuration
    public Config getRunningConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        loadMixins();
        config = new Config("config.toml");
        System.out.println(config);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI is not installed!");
        }

        dispatcher = new PacketEventDispatcher(this);
        violationManagers = new ArrayList<>();
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> violationManagers.forEach(ViolationManager::decrementAll), 0, 1, TimeUnit.SECONDS);
        commandManager = new CommandManager();
        commandManager.registerCommands();
        duelManager = new DuelManager();
        arenaManager = new ArenaManager();
        boundingBoxManager = new BoundingBoxManager();
        portalManager = new PortalManager();
        kitManager = new KitManager();
        eventRegister = new EventManager();
        eventRegister.registerEvents();
        npcManager = new NPCManager();
        TaskManager.register(TabListUpdater.class);
        TaskManager.register(ArenaCleaner.class);

        // Make all worlds generate nothing but void chunks
        Bukkit.getWorlds().forEach(world -> {

            WorldServer worldServer = ((CraftWorld) world).getHandle();
            try {
                Field chunkGenF = World.class.getDeclaredField("chunkProvider");
                chunkGenF.setAccessible(true);
                chunkGenF.set(worldServer, new ChunkProviderServer(worldServer, worldServer.getDataManager().createChunkLoader(worldServer.worldProvider), new TimedChunkGenerator(worldServer, new VoidGen(worldServer))));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
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

    public void registerListener(Listener listener) {
        if (ClassProcessor.hasAnnotation(listener)) ClassProcessor.process(listener);
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @SafeVarargs
    public final void registerPacketListener(PacketListener listener, Class<? extends Packet<?>>... packets) {
        if (ClassProcessor.hasAnnotation(listener)) ClassProcessor.process(listener);
        dispatcher.register(listener, packets);
    }

    public void registerViolationManager(ViolationManager violationManager) {
        if (violationManagers.contains(violationManager)) return;
        violationManagers.add(violationManager);
    }
}