package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.CommandManager;
import me.ian.command.PluginCommand;
import me.ian.utils.PlayerUtils;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends PluginCommand implements CommandExecutor {
    public HelpCommand() {
        super("help", false);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        PlayerUtils.sendHelpMessage(player);

        return true;
    }
}
