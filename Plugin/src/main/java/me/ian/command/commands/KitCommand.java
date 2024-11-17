package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.kits.Kit;
import me.ian.kits.KitManager;
import me.ian.utils.NBTUtils;
import me.ian.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class KitCommand extends PluginCommand implements CommandExecutor {
    public KitCommand() {
        super("kit", false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        KitManager kitManager = PVPHelper.INSTANCE.getKitManager();

        // TODO: implement kit system
        switch (label) {
            case "kit":
                // TODO: eventually implement a new GUI system if there are no command arguments (ignore for now)
                if (args.length == 0) {
                    Utils.sendMessage(player, "&cMust enter a name for the kit.");
                    break;
                }

                // Attempt to retrieve the kit from user-specific or global kits
                Kit kit = Optional.ofNullable(kitManager.getKit(player.getUniqueId(), args[0]))
                        .orElse(kitManager.getKit(null, args[0]));

                if (kit != null) {
                    kit.equip(player);
                } else {
                    Utils.sendMessage(player, String.format("&cKit %s does not exist", args[0]));
                }
                break;

            case "createukit":
                if (args.length == 0) {
                    Utils.sendMessage(player, "&cMust enter a name for the kit.");
                    break;
                }

                // Create and save the user kit
                Kit userKit = new Kit(args[0], NBTUtils.getPlayerInventoryAsTag(player), player.getUniqueId());
                kitManager.saveKit(userKit);

                // Add the kit to the player's kit list
                kitManager.getUserKits()
                        .computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>())
                        .add(userKit);

                Utils.sendMessage(player, String.format("&bCreated user kit &a%s", userKit.getName()));

                break;

            case "creategkit":
                if (args.length == 0) {
                    Utils.sendMessage(player, "&cMust enter a name for the global kit.");
                    break;
                }

                // Create and save the global kit
                Kit globalKit = new Kit(args[0], NBTUtils.getPlayerInventoryAsTag(player), null);
                kitManager.saveKit(globalKit);

                // Add the kit to the global kit list
                kitManager.getGlobalKits().add(globalKit);

                Utils.sendMessage(player, String.format("&bCreated global kit &a%s", globalKit.getName()));
                break;

            case "removeukit":
                if (args.length == 0) {
                    Utils.sendMessage(player, "&cMust enter a name for the user kit to remove.");
                    break;
                }

                // Try to retrieve the user's kit
                Kit userKitToRemove = kitManager.getKit(player.getUniqueId(), args[0]);
                if (userKitToRemove != null) {
                    // Remove the kit from the player's list and delete it
                    kitManager.getUserKits().get(player.getUniqueId()).remove(userKitToRemove);
                    kitManager.removeKit(userKitToRemove);

                    Utils.sendMessage(player, String.format("&bRemoved user kit &a%s", userKitToRemove.getName()));
                } else {
                    Utils.sendMessage(player, String.format("&cUser kit %s does not exist", args[0]));
                }
                break;

            case "removegkit":
                if (args.length == 0) {
                    Utils.sendMessage(player, "&cMust enter a name for the global kit to remove.");
                    break;
                }

                // Try to retrieve the global kit
                Kit globalKitToRemove = kitManager.getKit(null, args[0]);
                if (globalKitToRemove != null) {
                    // Remove the kit from the global list and delete it
                    kitManager.getGlobalKits().remove(globalKitToRemove);
                    kitManager.removeKit(globalKitToRemove);

                    Utils.sendMessage(player, String.format("&bRemoved global kit &a%s", globalKitToRemove.getName()));
                } else {
                    Utils.sendMessage(player, String.format("&cGlobal kit %s does not exist", args[0]));
                }
                break;
        }
        return true;
    }
}
