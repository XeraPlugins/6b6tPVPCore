package me.ian.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        // TODO: implement kit system
        switch (label) {
            case "kit":

                break;

            case "createukit":

                break;

            case "creategkit":

                break;

            case "removeukit":

                break;

            case "removegkit":

                break;
        }
        return true;
    }
}
