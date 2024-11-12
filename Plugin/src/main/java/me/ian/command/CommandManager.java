package me.ian.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ian.PVPHelper;
import me.ian.command.commands.CreateArena;
import me.ian.command.commands.FacePlayer;
import me.ian.command.commands.ReloadConfig;
import me.ian.command.commands.SpawnNPC;
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
        commands.add(new ReloadConfig());
        commands.add(new FacePlayer());
        commands.add(new SpawnNPC());
        commands.add(new CreateArena());
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
}
