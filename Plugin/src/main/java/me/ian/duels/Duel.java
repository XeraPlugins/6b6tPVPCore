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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Getter
@Setter
@RequiredArgsConstructor
public class Duel {

    private final Arena arena;
    private final List<Player> participants;
    private boolean active = false;
    private boolean winnerDeclared = false;

    public void start() {
        participants.get(0).teleport(arena.getWorld().getHighestBlockAt(arena.getPointA()).getLocation());
        participants.get(1).teleport(arena.getWorld().getHighestBlockAt(arena.getPointB()).getLocation());
        PlayerUtils.facePlayersTowardsEachOther(participants.get(0), participants.get(1));

        new Timer().scheduleAtFixedRate(new TimerTask() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown == 0) {
                    participants.forEach(player -> PlayerUtils.sendTitle(player, "&e&lGO!", ""));
                    setActive(true);
                    this.cancel();
                } else {
                    participants.forEach(player -> PlayerUtils.sendTitle(player, String.format("&e%s &7vs. &e%s", participants.get(0).getName(), participants.get(1).getName()), String.format("&bMatch starts in &a%s &bseconds...", countdown)));
                    countdown--;
                }
            }
        }, 1000L, 1000L);
    }

    public void declareWinner(Player player) {
        if (!participants.contains(player)) return;
        setWinnerDeclared(true);
        Utils.broadcastMessage(String.format("&a%s &bhas won the duel with &a%s &bhealth left", player.getName(), player.getHealth()));
        player.getWorld().spawn(player.getLocation(), Firework.class);
        Bukkit.getScheduler().runTaskLater(PVPHelper.INSTANCE, () -> {
            participants.forEach(participant -> {
                if (participant.isOnline()) {
                    participant.setGameMode(GameMode.SURVIVAL);
                    participant.setHealth(participant.getMaxHealth());
                    participant.getInventory().clear();
                    PlayerUtils.teleportToSpawn(participant);
                }
            });
            arena.clear();
        }, 20 * 5L);
    }
}
