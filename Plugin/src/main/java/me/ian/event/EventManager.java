package me.ian.event;

import me.ian.PVPHelper;
import me.ian.arena.ArenaManager;
import me.ian.event.listeners.BedPlacementListener;
import me.ian.event.listeners.PlayerDeathListener;
import me.ian.event.listeners.patches.*;
import me.ian.event.listeners.ItemRevertListener;
import me.ian.lobby.LobbyProtection;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SevJ6
 */
public class EventManager {

    private final List<Listener> listeners;

    public EventManager() {
        listeners = new ArrayList<>();

        // add listeners
        listeners.add(new PlayerDeathListener());
        listeners.add(new Boatfly());
        listeners.add(new EntityCollisions());
        listeners.add(new ProjectileCrash());
        listeners.add(new ProjectileVelocity());
        listeners.add(new DispenserCrash());
        listeners.add(new ItemRevertListener());
        listeners.add(new LobbyProtection());
        listeners.add(new BedPlacementListener());
        listeners.add(PVPHelper.INSTANCE.getArenaManager());
    }

    public void registerEvents() {
        listeners.forEach(listener -> PVPHelper.INSTANCE.registerListener(listener));
    }
}
