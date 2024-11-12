package me.ian.event;

import me.ian.PVPHelper;
import me.ian.event.listeners.PlayerDeathListener;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<Listener> listeners;

    public EventManager() {
        listeners = new ArrayList<>();

        // add listeners
        listeners.add(new PlayerDeathListener());
    }

    public void registerEvents() {
        listeners.forEach(listener -> PVPHelper.INSTANCE.getServer().getPluginManager().registerEvents(listener, PVPHelper.INSTANCE));
    }
}
