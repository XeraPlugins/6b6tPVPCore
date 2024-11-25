package me.ian.lobby.frames.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class PlaceItemInFrameEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemFrame frame;
    private boolean cancelled;
    private ItemStack item;

    public PlaceItemInFrameEvent(Player player, ItemFrame frame, ItemStack item) {
        this.player = player;
        this.frame = frame;
        this.item = item;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}