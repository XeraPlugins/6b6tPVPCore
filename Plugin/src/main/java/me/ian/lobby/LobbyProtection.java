package me.ian.lobby;

import com.moandjiezana.toml.Toml;
import me.ian.PVPHelper;
import me.ian.arena.ArenaManager;
import me.ian.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) return;
        PlayerUtils.teleportToSpawn(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerUtils.teleportToSpawn(player);
    }
}
