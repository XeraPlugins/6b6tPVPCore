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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreDeath(PlayerPreDeathEvent event) {
        handleDeclaration(event.getPlayer().getBukkitEntity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        handleDeclaration(event.getPlayer());
    }

    private Duel findDuelByPlayer(Player player) {
        return duels.stream()
                .filter(duel -> duel.getParticipants().contains(player))
                .findFirst()
                .orElse(null); // Return null if no duel found
    }

    private void handleDeclaration(Player player) {
        Duel duel = findDuelByPlayer(player);

        if (duel != null) {
            duel.setActive(false);

            // Declare the winner for the other participant
            duel.getParticipants().stream()
                    .filter(participant -> !participant.equals(player))
                    .findFirst().ifPresent(duel::declareWinner);

            duels.remove(duel);
        }
    }
}
