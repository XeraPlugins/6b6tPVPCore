package me.ian.command.commands;

import me.ian.command.PluginCommand;
import me.ian.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand extends PluginCommand implements CommandExecutor {
    public RenameCommand() {
        super("rename", false, false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length < 1) {
            Utils.sendMessage(player, "&cPlease enter a name that you would like to rename your item with");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) {
            Utils.sendMessage(player, "&cYou must be holding an item");
            return true;
        }

        // Parse name
        String name = String.join(" ", args);
        name = name.substring(0, Math.min(name.length(), 35));
        name = Utils.translateChars(name);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        player.updateInventory();

        Utils.sendMessage(player, "&aSuccessfully updated the display name for the item you are holding");

        return true;
    }
}
