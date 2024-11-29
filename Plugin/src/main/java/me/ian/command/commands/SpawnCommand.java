package me.ian.command.commands;

import me.ian.command.PluginCommand;
import me.ian.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends PluginCommand implements CommandExecutor {

    public SpawnCommand() {
        super("spawn", false, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        PlayerUtils.teleportToSpawn(player);
        return true;
    }
}
