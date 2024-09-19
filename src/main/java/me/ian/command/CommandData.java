package me.ian.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;

/**
 * @author SevJ6
 */
@Getter
public class CommandData {

    private final String commandName;
    private final boolean adminOnly;

    @Setter
    private CommandExecutor commandExecutor;

    public CommandData(String commandName, boolean adminOnly) {
        this.commandName = commandName;
        this.adminOnly = adminOnly;
        commandExecutor = (CommandExecutor) this;
    }
}
