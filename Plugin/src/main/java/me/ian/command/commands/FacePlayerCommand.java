package me.ian.command.commands;

import me.ian.command.PluginCommand;
import me.ian.utils.PlayerUtils;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author SevJ6
 */
public class FacePlayerCommand extends PluginCommand implements CommandExecutor {

    public FacePlayerCommand() {
        super("faceplayer", true, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (!(args.length > 0)) {
            Utils.sendMessage(sender, command.getUsage());
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Utils.sendMessage(sender, String.format("%s is not online", args[0]));
            return true;
        }

        PlayerUtils.facePlayersTowardsEachOther(player, target);
        return true;
    }
}
