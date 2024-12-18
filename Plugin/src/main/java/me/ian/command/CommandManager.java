package me.ian.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ian.PVPHelper;
import me.ian.command.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author SevJ6
 */
@RequiredArgsConstructor
@Getter
public class CommandManager {

    private final List<PluginCommand> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        commands.add(new ReloadConfigCommand());
        commands.add(new FacePlayerCommand());
        commands.add(new NPCCommand());
        commands.add(new ArenaCommand());
        commands.add(new DuelCommand());
        commands.add(new KitCommand());
        commands.add(new WorldSwitchCommand());
        commands.add(new HelpCommand());
        commands.add(new WipeEntitiesCommand());
        commands.add(new RenameCommand());
        commands.add(new PortalCommand());
        commands.add(new PluginToolsCommand());
        commands.add(new SpawnCommand());
    }

    public void registerCommands() {
        commands.forEach(this::registerCommand);
    }

    public void registerCommand(PluginCommand command) {
        org.bukkit.command.PluginCommand bukkitCommand = Bukkit.getPluginCommand(command.getCommandName());
        if (command.isAdminOnly()) bukkitCommand.setPermission("commands.administrator");
        CommandExecutor executor = command.getCommandExecutor();
        bukkitCommand.setExecutor(executor);

        if (command.getCommandExecutor() instanceof TabExecutor) {
            bukkitCommand.setTabCompleter((TabCompleter) executor);
        }

        PVPHelper.INSTANCE.getLogger().log(Level.INFO, String.format("Registered command %s", command.getCommandName()));
    }

    public PluginCommand getCommand(String name) {
        return commands.stream()
                .filter(pluginCommand -> pluginCommand.getCommandName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
