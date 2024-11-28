package me.ian.general.listeners;

import me.ian.PVPHelper;
import me.ian.command.CommandManager;
import me.ian.command.PluginCommand;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
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
        String commandRan = event.getMessage().substring(1).split(" ")[0];
        Player player = event.getPlayer();
        if (commandRan.equalsIgnoreCase("lobby")) return;

        if (commandRan.equalsIgnoreCase("kill")) {
            event.setCancelled(true);
            player.setLastDamageCause(null);
            player.setHealth(0.0D);
            Utils.broadcastMessage(String.format("&3%s &4killed themselves.", player.getName()));
            return;
        }

        if (event.getPlayer().isOp()) return;

        CommandManager commandManager = PVPHelper.INSTANCE.getCommandManager();

        PluginCommand commandFound = commandManager.getCommand(commandRan);
        if (commandFound == null) {
            commandFound = commandManager.getCommands().stream()
                    .filter(pluginCommand -> !pluginCommand.isAdminOnly())
                    .filter(cmd -> {
                        org.bukkit.command.PluginCommand bukkitCommand = Bukkit.getPluginCommand(cmd.getCommandName());
                        return bukkitCommand != null && (bukkitCommand.getName().equalsIgnoreCase(commandRan) || bukkitCommand.getAliases().contains(commandRan));
                    }).findAny().orElse(null);
        }

        boolean cancelled = false;
        if (commandFound == null) {
            cancelled = true;
            Utils.sendMessage(player, "&cUnknown command. Type /help for a list of all commands");
        } else if (!commandFound.isArenaAllowed() && PVPHelper.INSTANCE.getArenaManager().isPlayerInArena(player)) {
            cancelled = true;
            Utils.sendMessage(player, "&cUnable to run that command inside an arena. Try again later.");
        }

        event.setCancelled(cancelled);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTab(TabCompleteEvent event) {
        if (event.getSender() instanceof Player && !event.getSender().isOp() && event.getBuffer().equals("/")) {
            event.setCompletions(PVPHelper.INSTANCE.getCommandManager().getCommands().stream().filter(pluginCommand -> !pluginCommand.isAdminOnly()).map(pluginCommand -> "/".concat(pluginCommand.getCommandName())).collect(Collectors.toList()));
        }
    }
}
