package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.duels.Duel;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DuelCommand extends PluginCommand implements CommandExecutor {
    public DuelCommand() {
        super("duel", false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                Player opponent = Bukkit.getPlayer(args[0]);
                if (opponent != null && opponent.isOnline()) {
                    Duel duel = new Duel(PVPHelper.INSTANCE.getArenaManager().getArenas().get(0), Arrays.asList(player, opponent));
                    PVPHelper.INSTANCE.getDuelManager().getDuels().add(duel);
                    duel.start();
                } Utils.sendMessage(player, String.format("&c%s is not online", args[0]));
            } else Utils.sendMessage(player, command.getUsage());
        }
        return true;
    }
}
