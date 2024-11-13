package me.ian.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    public static boolean isIllegal(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (!itemStack.hasItemMeta()) return false;
        if (!itemStack.getItemMeta().hasEnchants()) return false;
        return itemStack.getItemMeta().getEnchants().entrySet().stream().anyMatch(entry -> entry.getValue() > entry.getKey().getMaxLevel());
    }

    public static void revertItemStack(ItemStack itemStack) {
        if (itemStack == null) return;
        if (!itemStack.hasItemMeta()) return;
        if (!itemStack.getItemMeta().hasEnchants()) return;
        ItemMeta meta = itemStack.getItemMeta();
        meta.getEnchants().forEach((key, value) -> {
            if (value > key.getMaxLevel()) {
                meta.removeEnchant(key);
                meta.addEnchant(key, key.getMaxLevel(), true);
            }
        });
        itemStack.setItemMeta(meta);
    }

    public static void revertInventory(Inventory inventory) {
        for (ItemStack content : inventory.getContents()) {
            revertItemStack(content);
        }
    }
}
