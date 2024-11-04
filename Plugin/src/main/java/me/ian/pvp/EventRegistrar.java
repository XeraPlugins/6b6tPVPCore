package me.ian.pvp;

import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SevJ6
 * */
public class EventRegistrar {

    private final List<Listener> listeners;

    public EventRegistrar() {
        listeners = new ArrayList<>();

    }

    public void registerAllListeners() {

    }
}
