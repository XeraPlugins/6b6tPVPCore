package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author SevJ6
 */
public class ReloadConfigCommand extends PluginCommand implements CommandExecutor {

    public ReloadConfigCommand() {
        super("reloadconfig", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PVPHelper.INSTANCE.getRunningConfig().loadConfig();
        Utils.sendMessage(sender, String.format("&a%s", "Configuration Reloaded."));
        return true;
    }
}
