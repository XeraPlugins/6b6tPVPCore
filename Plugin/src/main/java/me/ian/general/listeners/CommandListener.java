package me.ian.general.listeners;

import me.ian.PVPHelper;
import me.ian.command.CommandManager;
import me.ian.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.stream.Collectors;

public class CommandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp()) return;

        String command = event.getMessage().substring(1).split(" ")[0];
        CommandManager commandManager = PVPHelper.INSTANCE.getCommandManager();

        boolean isAllowedCommand = commandManager.getCommands().stream()
                .filter(pluginCommand -> !pluginCommand.isAdminOnly()) // Only non-admin commands
                .anyMatch(pluginCommand -> {
                    PluginCommand bukkitCommand = Bukkit.getPluginCommand(pluginCommand.getCommandName());
                    return bukkitCommand != null &&
                            (bukkitCommand.getName().equalsIgnoreCase(command) || bukkitCommand.getAliases().contains(command));
                });

        if (!isAllowedCommand) {
            event.setCancelled(true);
            PlayerUtils.sendHelpMessage(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTab(TabCompleteEvent event) {
        if (event.getSender() instanceof Player && !event.getSender().isOp() && event.getBuffer().equals("/")) {
            event.setCompletions(PVPHelper.INSTANCE.getCommandManager().getCommands().stream().filter(pluginCommand -> !pluginCommand.isAdminOnly()).map(pluginCommand -> "/".concat(pluginCommand.getCommandName())).collect(Collectors.toList()));
        }
    }
}
