package me.ian.lobby;

import com.moandjiezana.toml.Toml;
import me.ian.PVPHelper;
import me.ian.arena.ArenaManager;
import me.ian.utils.PlayerUtils;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class LobbyProtection implements Listener {

    private final ArenaManager arenaManager = PVPHelper.INSTANCE.getArenaManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().isOp()) return;
        event.setCancelled(!arenaManager.isPlayerInArena(event.getPlayer()) || !arenaManager.isLocationInArena(event.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) return;
        event.setCancelled(!arenaManager.isPlayerInArena(event.getPlayer()) || !arenaManager.isLocationInArena(event.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(EntityExplodeEvent event) {
        if (event.blockList().stream().anyMatch(block -> !arenaManager.isLocationInArena(block.getLocation())))
            event.blockList().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) return;
        PlayerUtils.teleportToSpawn(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Toml config = PVPHelper.INSTANCE.getRunningConfig().getToml().getTable("lobby_spawn");
        event.setRespawnLocation(new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"), config.getDouble("y"), config.getDouble("z"), config.getDouble("yaw").floatValue(), config.getDouble("pitch").floatValue()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity && !arenaManager.isLocationInArena(event.getEntity().getLocation().getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (((Player) event.getEntity()).getHealth() - event.getDamage() <= 0.0D) event.setDamage(0.0D);
        }
    }
}
