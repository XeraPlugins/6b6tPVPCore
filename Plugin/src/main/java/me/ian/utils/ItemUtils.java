package me.ian.utils;

import me.ian.duels.Duel;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemUtils {

    public static final Map<Integer, ItemStack> ITEM_INDEX = new HashMap<Integer, ItemStack>() {{
        put(1, toItem(Material.END_CRYSTAL));
        put(2, toItem(Material.EXP_BOTTLE));
        put(3, toItem(Material.TOTEM));
        put(4, toItem(Material.OBSIDIAN));
        put(5, toItem(Material.CHORUS_FRUIT));
        put(6, toItem(Material.ENDER_PEARL));
        put(7, toItem(Material.GOLDEN_APPLE, (short) 1));
        put(8, toItem(Material.DISPENSER));
        put(9, toItem(Material.REDSTONE_BLOCK));
        put(10, toItem(Material.HOPPER));
        put(11, toItem(Material.DIAMOND_HELMET, "&aProtection Helmet", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(12, toItem(Material.DIAMOND_CHESTPLATE, "&aProtection Chestplate", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(13, toItem(Material.DIAMOND_LEGGINGS, "&aProtection Leggings", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(14, toItem(Material.DIAMOND_BOOTS, "&aProtection Boots", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(15, toItem(Material.DIAMOND_HELMET, "&cBlast-Prot Helmet", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(16, toItem(Material.DIAMOND_CHESTPLATE, "&cBlast-Prot Chestplate", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(17, toItem(Material.DIAMOND_LEGGINGS, "&cBlast-Prot Leggings", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(18, toItem(Material.DIAMOND_BOOTS, "&cBlast-Prot Boots", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(19, toItem(Material.DIAMOND_SWORD, "Diamond Sword", Enchantment.DAMAGE_ALL, Enchantment.DURABILITY, Enchantment.SWEEPING_EDGE, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(20, toItem(Material.DIAMOND_PICKAXE, "Diamond Pickaxe", Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
    }};

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

    private static ItemStack toItem(Material material) {
        return new ItemStack(material, material.getMaxStackSize());
    }

    private static ItemStack toItem(Material material, String name, Enchantment... enchantments) {
        ItemStack item = new ItemStack(material, material.getMaxStackSize());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.translateChars(name));
        for (Enchantment enchantment : enchantments) {
            meta.addEnchant(enchantment, enchantment.getMaxLevel(), true);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack toItem(Material material, short durability) {
        return new ItemStack(material, material.getMaxStackSize(), durability);
    }
}
