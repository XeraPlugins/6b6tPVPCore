package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.kits.Kit;
import me.ian.kits.KitManager;
import me.ian.kits.gui.KitGuiTypeSelector;
import me.ian.utils.NBTUtils;
import me.ian.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KitCommand extends PluginCommand implements CommandExecutor {
    public KitCommand() {
        super("kit", false, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        KitManager kitManager = PVPHelper.INSTANCE.getKitManager();

        switch (label) {
            case "kit":
                if (args.length == 0) {
                    KitGuiTypeSelector guiTypeSelector = new KitGuiTypeSelector(player, kitManager);
                    guiTypeSelector.open();
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

                final String KIT_NAME = args[0];

                // Check if the player already has a kit with the same name
                List<Kit> playerKits = kitManager.getUserKits().get(player.getUniqueId());
                if (playerKits != null && playerKits.stream().anyMatch(k -> k.getName().equalsIgnoreCase(KIT_NAME))) {
                    Utils.sendMessage(player, String.format("&cYou already have a user kit named &a%s&c.", KIT_NAME));
                    break;
                }

                // Check if the kit name already exists globally
                if (kitManager.getKit(null, KIT_NAME) != null) {
                    Utils.sendMessage(player, String.format("&cA global kit with the name &a%s&c already exists.", KIT_NAME));
                    break;
                }

                // Check if the player has reached the kit limit
                int limit = PVPHelper.INSTANCE.getRunningConfig().getToml().getLong("user_kit_limit").intValue();
                if (playerKits != null && playerKits.size() >= limit) {
                    Utils.sendMessage(player, String.format("&cYou can only have up to %s user kits. Please remove a kit before creating a new one.", limit));
                    break;
                }

                // Create and save the user kit
                Kit userKit = new Kit(KIT_NAME, NBTUtils.getPlayerInventoryAsTag(player), player.getUniqueId());
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

                final String GKIT_NAME = args[0];

                // Check if a global kit with the same name already exists
                if (kitManager.getKit(null, GKIT_NAME) != null) {
                    Utils.sendMessage(player, String.format("&cA global kit with the name &a%s&c already exists.", GKIT_NAME));
                    break;
                }

                // Create and save the global kit
                Kit globalKit = new Kit(GKIT_NAME, NBTUtils.getPlayerInventoryAsTag(player), null);
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

            case "listukits":
                // List all user kits for the player
                List<Kit> userKits = kitManager.getUserKits().get(player.getUniqueId());
                if (userKits == null || userKits.isEmpty()) {
                    Utils.sendMessage(player, "&cYou have no user kits.");
                } else {
                    StringBuilder kitsList = new StringBuilder("&bYour User Kits:\n");
                    for (Kit k : userKits) {
                        kitsList.append(String.format("&a- %s\n", k.getName()));
                    }
                    Utils.sendMessage(player, kitsList.toString());
                }
                break;

            case "listgkits":
                // List all global kits
                List<Kit> globalKits = kitManager.getGlobalKits();
                if (globalKits.isEmpty()) {
                    Utils.sendMessage(player, "&cNo global kits available.");
                } else {
                    StringBuilder kitsList = new StringBuilder("&bGlobal Kits:\n");
                    for (Kit k : globalKits) {
                        kitsList.append(String.format("&a- %s\n", k.getName()));
                    }
                    Utils.sendMessage(player, kitsList.toString());
                }
                break;
        }
        return true;
    }
}
