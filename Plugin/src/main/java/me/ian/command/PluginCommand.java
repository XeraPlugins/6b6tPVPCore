package me.ian.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;

/**
 * @author SevJ6
 */
@Getter
public class PluginCommand {

    private final String commandName;
    private final boolean adminOnly;
    private final boolean arenaAllowed;

    @Setter
    private CommandExecutor commandExecutor;

    public PluginCommand(String commandName, boolean adminOnly, boolean arenaAllowed) {
        this.commandName = commandName;
        this.adminOnly = adminOnly;
        this.arenaAllowed = arenaAllowed;
        commandExecutor = (CommandExecutor) this;
    }
}
