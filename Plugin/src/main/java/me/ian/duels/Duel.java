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

@Getter
@Setter
@RequiredArgsConstructor
public class Duel {

    private final Arena arena;
    private final List<Player> participants;
    private boolean active = false;
    private boolean winnerDeclared = false;

    public void declareWinner(Player player) {
        if (!participants.contains(player)) return;
        setWinnerDeclared(true);
        Utils.broadcastMessage(String.format("&a%s &bhas won the duel with &a%s &bhealth left", player.getName(), player.getHealth()));
        player.getWorld().spawn(player.getLocation(), Firework.class);
        Bukkit.getScheduler().runTaskLater(PVPHelper.INSTANCE, () -> {
            participants.forEach(participant -> {
                participant.setGameMode(GameMode.SURVIVAL);
                participant.setHealth(participant.getMaxHealth());
                participant.getInventory().clear();
                PlayerUtils.teleportToSpawn(participant);
            });
            arena.clear();
        }, 20 * 5L);
    }
}
