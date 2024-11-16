package me.ian.duels;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.arena.Arena;
import me.ian.mixin.event.PlayerPreDeathEvent;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DuelManager implements Listener {

    private final List<Duel> duels = new ArrayList<>();
    private final List<Arena> duelArenas = new ArrayList<>();

    public DuelManager() {
        PVPHelper.INSTANCE.registerListener(this);
    }

    public Arena findEmptyArena() {
        return duelArenas.stream()
                .filter(arena -> duels.stream().noneMatch(duel -> duel.getArena() == arena))
                .findFirst()
                .orElse(null);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        event.setCancelled(duels.stream().anyMatch(duel -> duel.getParticipants().contains(event.getPlayer()) && !duel.isActive() && !duel.isWinnerDeclared()));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(event.getEntity() instanceof Player && duels.stream().anyMatch(duel -> duel.getParticipants().contains((Player) event.getEntity()) && !duel.isActive()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(duels.stream().anyMatch(duel -> duel.getParticipants().contains(event.getPlayer()) && !duel.isActive() && !duel.isWinnerDeclared()));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(duels.stream().anyMatch(duel -> duel.getParticipants().contains(event.getPlayer()) && !duel.isActive() && !duel.isWinnerDeclared()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreDeath(PlayerPreDeathEvent event) {
        Player player = event.getPlayer().getBukkitEntity();
        duels.stream().filter(duel -> duel.getParticipants().contains(player)).findFirst().ifPresent(duel -> {
            duel.setActive(false);
            event.setCancelled(true);
            player.setGameMode(GameMode.SPECTATOR);
            for (Player participant : duel.getParticipants()) {
                if (participant == player) continue;
                duel.declareWinner(participant);
            }
            duels.remove(duel);
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        duels.stream().filter(duel -> duel.getParticipants().contains(player)).findFirst().ifPresent(duel -> {
            duel.setActive(false);
            for (Player participant : duel.getParticipants()) {
                if (participant == player) continue;
                duel.declareWinner(participant);
            }
            duels.remove(duel);
        });
    }
}
