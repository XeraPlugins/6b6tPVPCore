package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.lobby.npc.InteractionBehavior;
import me.ian.lobby.npc.NPC;
import me.ian.lobby.npc.NPCManager;
import me.ian.utils.PlayerUtils;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author SevJ6
 */
public class NPCCommand extends PluginCommand implements CommandExecutor {
    public NPCCommand() {
        super("npc", true, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            Utils.sendMessage(player, "&cUsage: /npc <create|remove> <name> [options...]");
            return true;
        }

        NPCManager npcManager = PVPHelper.INSTANCE.getNpcManager();
        String action = args[0].toLowerCase();
        String name = args[1];

        switch (action) {
            case "create":
                // Validate that the NPC name is unique
                if (npcManager.getNPC(name) != null) {
                    Utils.sendMessage(player, "&cAn NPC with this name already exists!");
                    return true;
                }

                // Parse behavior if provided
                InteractionBehavior behavior = InteractionBehavior.NONE; // Default behavior
                if (args.length > 2) {
                    try {
                        behavior = InteractionBehavior.valueOf(args[2].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        Utils.sendMessage(player, "&cInvalid behavior specified! Available behaviors: " +
                                Arrays.stream(InteractionBehavior.values())
                                        .map(Enum::name)
                                        .collect(Collectors.joining(", ")));
                        return true;
                    }
                }

                // Parse optional skin UUID
                String skinUuid = args.length > 3 ? args[3] : PVPHelper.INSTANCE.getRunningConfig().getToml().getString("default_skin_uuid");

                // Handle arena name if behavior is SEND_TO_ARENA
                String arenaName = null;
                if (behavior == InteractionBehavior.SEND_TO_ARENA) {
                    if (args.length < 5) {
                        Utils.sendMessage(player, "&cYou must specify an arena name when using the SEND_TO_ARENA behavior!");
                        return true;
                    }
                    arenaName = args[4];

                    // Validate that the arena exists
                    if (PVPHelper.INSTANCE.getArenaManager().getArena(arenaName) == null) {
                        Utils.sendMessage(player, "&cThe specified arena '" + arenaName + "' does not exist!");
                        return true;
                    }
                }

                NPC npc = new NPC(
                        player.getLocation(),
                        name,
                        PlayerUtils.getSkinProperties(skinUuid),
                        true,
                        behavior
                );

                // Save the arena name to the NPC's NBT data if applicable
                if (arenaName != null) {
                    NBTTagCompound data = npc.getData();
                    data.setString("arena_endpoint", arenaName);
                    npc.setData(data);
                }

                npcManager.createNPC(npc);
                Utils.sendMessage(player, "&aNPC '" + name + "' created with behavior '" + behavior.name() + "'.");
                break;

            case "remove":
                if (npcManager.getNPC(name) == null) {
                    Utils.sendMessage(player, "&cNo NPC found with the name '" + name + "'.");
                    return true;
                }

                npcManager.removeNPC(name);
                Utils.sendMessage(player, "&aNPC '" + name + "' has been removed.");
                break;

            default:
                Utils.sendMessage(player, "&cUsage: /npc <create|remove> <name> [options...]");
                break;
        }

        return true;
    }

}



