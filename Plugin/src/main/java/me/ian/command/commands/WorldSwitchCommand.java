package me.ian.command.commands;

import me.ian.command.PluginCommand;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSwitchCommand extends PluginCommand implements CommandExecutor {
    public WorldSwitchCommand() {
        super("world", true, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        Location location = player.getLocation();

        if (args.length < 1) {
            Utils.sendMessage(player, "&cMust enter a world name!");
            return true;
        }

        String input = args[0].toLowerCase();
        World worldTo = Bukkit.getWorld(input.equals("overworld") ? "world" : input.equals("nether") ? "world_nether" : input.equals("end") ? "world_the_end" : input);
        if (worldTo == null) {
            Utils.sendMessage(player, "&cInvalid world");
            return true;
        }

        World worldFrom = player.getWorld();

        if ((worldFrom.getEnvironment() == World.Environment.NETHER && worldTo.getEnvironment() == World.Environment.NORMAL) ||
                (worldFrom.getEnvironment() == World.Environment.NORMAL && worldTo.getEnvironment() == World.Environment.NETHER)) {
            double multiplier = worldFrom.getEnvironment() == World.Environment.NETHER ? 8 : 1 / 8.0;
            location.setX(location.getX() * multiplier);
            location.setZ(location.getZ() * multiplier);
        }

        location.setWorld(worldTo);
        player.teleport(location);

        return true;
    }
}
