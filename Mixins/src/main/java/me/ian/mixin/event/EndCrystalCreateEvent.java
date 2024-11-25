package me.ian.mixin.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class EndCrystalCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final EnderCrystal crystal;
    private final World world;
    private final Location location;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}