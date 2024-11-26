package me.ian.command.commands;

import me.ian.command.PluginCommand;
import me.ian.utils.ItemUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginToolsCommand extends PluginCommand implements CommandExecutor {

    public PluginToolsCommand() {
        super("tools", true, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        player.getInventory().addItem(ItemUtils.genSetter("boundBoxSetter"), ItemUtils.genSetter("exitPortalSetter"));

        return true;
    }
}
