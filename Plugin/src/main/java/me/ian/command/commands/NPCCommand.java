package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.lobby.npc.NPC;
import me.ian.lobby.npc.NPCManager;
import me.ian.lobby.npc.custom.ItemVendor;
import me.ian.utils.PlayerUtils;
import me.ian.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author SevJ6
 */
public class NPCCommand extends PluginCommand implements CommandExecutor {
    public NPCCommand() {
        super("npc", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            Utils.sendMessage(player, "&cUsage: " + command.getUsage());
            return true;
        }

        NPCManager npcManager = PVPHelper.INSTANCE.getNpcManager();

        switch (args[0].toLowerCase()) {
            // Create NPCs
            case "create":
                if (args.length < 2) {
                    Utils.sendMessage(player, "&cPlease enter a name!");
                    break;
                }

                String name = args[1];
                String defaultSkinUuid = PVPHelper.INSTANCE.getRunningConfig().getToml().getString("default_skin_uuid");
                boolean isVendor = args.length > 2 && args[2].equalsIgnoreCase("vendor");

                if (npcManager.getNPC(name) != null) {
                    Utils.sendMessage(player, "&cAn NPC with that name already exists!");
                    break;
                }

                if (isVendor) {
                    ItemVendor vendor = new ItemVendor(
                            player.getLocation(),
                            name,
                            PlayerUtils.getSkinProperties(args.length > 3 ? args[3] : defaultSkinUuid),
                            true
                    );
                    npcManager.createNPC(vendor);
                    Utils.sendMessage(player, "&aItem Vendor NPC created with name: &e" + name);
                } else {
                    NPC npc = new NPC(
                            player.getLocation(),
                            name,
                            PlayerUtils.getSkinProperties(args.length > 2 ? args[2] : defaultSkinUuid),
                            true
                    ) {
                        @Override
                        public void onInteract(Player player) {
                            System.out.println("test");
                        }
                    };
                    npcManager.createNPC(npc);
                    Utils.sendMessage(player, "&aNPC created with name: &e" + name);
                }
                break;

            // Remove NPCs
            case "remove":
                if (args.length < 2) {
                    Utils.sendMessage(player, "&cPlease specify the name of the NPC to remove!");
                    break;
                }

                String npcNameToRemove = args[1];
                boolean removed = npcManager.removeNPC(npcNameToRemove);

                if (removed) {
                    Utils.sendMessage(player, "&aSuccessfully removed NPC: &e" + npcNameToRemove);
                } else {
                    Utils.sendMessage(player, "&cCould not remove NPC: &e" + npcNameToRemove + " &c(NPC not found or deletion failed).");
                }
                break;

            default:
                Utils.sendMessage(player, "&cUsage: " + command.getUsage());
                break;
        }

        return true;
    }


}
