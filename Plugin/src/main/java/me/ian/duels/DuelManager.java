package me.ian.duels;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.mixin.event.PlayerPreDeathEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DuelManager implements Listener {

    private final List<Duel> duels = new ArrayList<>();

    public DuelManager() {
        PVPHelper.INSTANCE.registerListener(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        event.setCancelled(duels.stream().anyMatch(duel -> !duel.isActive() && !duel.isWinnerDeclared() && duel.getParticipants().contains(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(event.getEntity() instanceof Player && duels.stream().anyMatch(duel -> !duel.isActive() && duel.getParticipants().contains((Player) event.getEntity())));
    }

    @EventHandler(priority = EventPriority.HIGH)
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
        });
    }
}
