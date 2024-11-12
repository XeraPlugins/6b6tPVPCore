package me.ian.lobby;

import me.ian.PVPHelper;
import me.ian.arena.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class LobbyProtection implements Listener {

    private final ArenaManager arenaManager = PVPHelper.INSTANCE.getArenaManager();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().isOp()) return;
        event.setCancelled(!arenaManager.isPlayerInArena(event.getPlayer()) || !arenaManager.isLocationInArena(event.getBlock().getLocation()));
    }

    @EventHandler
    public void onBreak(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) return;
        event.setCancelled(!arenaManager.isPlayerInArena(event.getPlayer()) || !arenaManager.isLocationInArena(event.getBlock().getLocation()));
    }

    @EventHandler
    public void onBreak(EntityExplodeEvent event) {
        if (event.blockList().stream().anyMatch(block -> !arenaManager.isLocationInArena(block.getLocation())))
            event.blockList().clear();
    }
}
