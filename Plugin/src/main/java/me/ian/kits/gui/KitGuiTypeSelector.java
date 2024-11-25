package me.ian.kits.gui;

import me.ian.kits.KitManager;
import me.ian.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitGuiTypeSelector extends KitGui {

    private final KitManager kitManager;

    public KitGuiTypeSelector(Player player, KitManager kitManager) {
        super(player, 9, Utils.getPrefix() + "&1Kits", null);
        this.kitManager = kitManager;
        ItemStack globalKitsItem = new ItemStack(Material.EMPTY_MAP);
        ItemMeta meta = globalKitsItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9&lGlobal Kits"));
        globalKitsItem.setItemMeta(meta);
        ItemStack userKitsItem = new ItemStack(Material.MAP);
        ItemMeta userItemMeta = userKitsItem.getItemMeta();
        userItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&lUser Kits &r&2(" + player.getName() + ")"));
        userKitsItem.setItemMeta(userItemMeta);
        getInventory().setItem(3, globalKitsItem);
        getInventory().setItem(5, userKitsItem);
    }

    @Override
    public void onSlotClick(int slot) {
        Player player = getPlayer();
        if (slot == 3) {
            KitGuiGlobal guiGlobal = new KitGuiGlobal(player, kitManager);
            guiGlobal.open();
        } else if (slot == 5) {
            KitGuiUser guiUser = new KitGuiUser(player, kitManager);
            guiUser.open();
        }
    }
}
