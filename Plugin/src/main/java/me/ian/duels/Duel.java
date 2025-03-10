package me.ian.duels;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.ian.PVPHelper;
import me.ian.arena.Arena;
import me.ian.utils.PlayerUtils;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class Duel {

    private final Arena arena;
    private final List<Player> participants;
    private final List<Player> spectators = new ArrayList<>();
    private boolean active = false;
    private boolean winnerDeclared = false;
    private final UUID identifier = UUID.randomUUID();

    public void start() {
        Player challenger = participants.get(0);
        Player opponent = participants.get(1);
        challenger.teleport(arena.getHighestSpot(arena.getBoundingBox().getPointA()).add(0.5, 0.0, 0.5));
        opponent.teleport(arena.getHighestSpot(arena.getBoundingBox().getPointB()).add(0.5, 0.0, 0.5));
        PlayerUtils.facePlayersTowardsEachOther(challenger, opponent);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown == 0) {
                    participants.forEach(player -> {
                        PlayerUtils.sendTitle(player, "&e&lGO!", "");
                        PlayerUtils.sendPling(player, 2f);
                    });
                    setActive(true);
                    this.cancel();
                } else {
                    participants.forEach(player -> {
                        PlayerUtils.sendTitle(player, String.format("&e%s &7vs. &e%s", challenger.getName(), opponent.getName()), String.format("&bMatch starts in &a%s &bseconds...", countdown));
                        PlayerUtils.sendPling(player, 1f);
                    });
                    countdown--;
                }
            }
        }, 1000L, 1000L);
    }

    public void declareWinner(Player player) {
        if (!participants.contains(player)) return;

        setWinnerDeclared(true);
        Utils.broadcastMessage(
                PVPHelper.INSTANCE.getRunningConfig().getToml().getString("duel_win")
                        .replace("%winner%", player.getName())
                        .replace("%health%", String.format("%.2f", player.getHealth()))
                        .replace("%max_health%", String.format("%.2f", player.getMaxHealth()))
        );

        player.getWorld().spawn(player.getLocation(), Firework.class);

        Bukkit.getScheduler().runTaskLater(PVPHelper.INSTANCE, () -> {
            arena.clear();

            if (player.isOnline()) {
                resetWinnerState(player);
            }

            spectators.stream()
                    .filter(Player::isOnline)
                    .forEach(this::resetSpectatorState);

            spectators.clear();
        }, 90L);
    }

    private void resetWinnerState(Player player) {
        PlayerUtils.teleportToSpawn(player);
        player.setHealth(player.getMaxHealth());
        player.getInventory().clear();
    }

    private void resetSpectatorState(Player spectator) {
        PlayerUtils.teleportToSpawn(spectator);
        spectator.setGameMode(GameMode.SURVIVAL);
    }

}
