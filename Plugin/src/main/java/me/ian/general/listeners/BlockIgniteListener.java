package me.ian.general.listeners;

import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

public class BlockIgniteListener implements Listener {

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        event.setCancelled(event.getIgnitingEntity() instanceof EnderCrystal && event.getIgnitingEntity().getWorld().getEnvironment().equals(World.Environment.THE_END));
    }
}
