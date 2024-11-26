package me.ian.command.commands;

import me.ian.command.PluginCommand;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class WipeEntitiesCommand extends PluginCommand implements CommandExecutor {

    // List of entities to skip when removing
    private static final List<EntityType> EXEMPT = Arrays.asList(
            EntityType.PLAYER,
            EntityType.ITEM_FRAME,
            EntityType.ARMOR_STAND
    );

    public WipeEntitiesCommand() {
        super("wipe", true, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getWorlds().forEach(world -> {
            Utils.broadcastMessage(String.format("&bRemoved &a%s &bentities from &a%s", world.getEntities().stream().filter(entity -> !EXEMPT.contains(entity.getType())).peek(Entity::remove).count(), world.getName()));
        });

        return true;
    }
}
