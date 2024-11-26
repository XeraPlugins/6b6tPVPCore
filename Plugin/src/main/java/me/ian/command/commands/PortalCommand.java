package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.portal.Portal;
import me.ian.portal.PortalManager;
import me.ian.utils.Utils;
import me.ian.utils.area.BoundingBox;
import me.ian.utils.area.BoundingBoxManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PortalCommand extends PluginCommand implements CommandExecutor {

    public PortalCommand() {
        super("portal", true, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            Utils.sendMessage(player, "&cUsage: " + command.getUsage());
            return true;
        }

        if (args.length < 2) {
            Utils.sendMessage(player, "&cMust enter a name for this portal!");
            return true;
        }

        String portalName = args[1];
        BoundingBoxManager boundingBoxManager = PVPHelper.INSTANCE.getBoundingBoxManager();
        PortalManager portalManager = PVPHelper.INSTANCE.getPortalManager();
        switch (args[0].toLowerCase()) {
            case "create":
                if (boundingBoxManager.getGlobalPointC() == null) {
                    Utils.sendMessage(player, "&cYou must set an exit location to create a portal");
                    break;
                }

                if (boundingBoxManager.getGlobalPointA() == null || boundingBoxManager.getGlobalPointB() == null) {
                    Utils.sendMessage(player, "&cYou must set the bounding box for this portal first");
                    break;
                }

                Portal portal = new Portal(
                        portalName,
                        new BoundingBox(
                                player.getWorld(),
                                boundingBoxManager.getGlobalPointA(),
                                boundingBoxManager.getGlobalPointB()
                        ),
                        boundingBoxManager.getGlobalPointC()
                );

                portalManager.create(portal);
                Utils.sendMessage(player, String.format("&aSuccessfully created portal '%s' with exit portal location '%s, %s, %s, %s'",
                        portalName,
                        portal.getBoundingBox().getWorld().getName(),
                        portal.getExitLocation().getX(),
                        portal.getExitLocation().getY(),
                        portal.getExitLocation().getZ()
                ));

                break;

            case "remove":
                Portal portalToRemove = portalManager.getPortals().stream().filter(p -> p.getName().equalsIgnoreCase(portalName)).findAny().orElse(null);
                if (portalToRemove == null) {
                    Utils.sendMessage(player, "&cPortal '" + portalName + "' does not exist");
                    break;
                }

                portalManager.delete(portalToRemove);
                Utils.sendMessage(player, "&aDeleted portal '" + portalToRemove.getName() + "'");

                break;

        }
        return true;
    }
}
