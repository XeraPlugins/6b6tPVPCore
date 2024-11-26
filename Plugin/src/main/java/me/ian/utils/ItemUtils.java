package me.ian.utils;

import com.sk89q.worldedit.blocks.BaseBlock;
import me.ian.duels.Duel;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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
        put(11, toItem(Material.WEB));
        put(12, toItem(Material.PISTON_BASE));
        put(13, toItem(Material.ANVIL));
        put(14, toItem(Material.DIAMOND_HELMET, "&aProtection Helmet", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(15, toItem(Material.DIAMOND_CHESTPLATE, "&aProtection Chestplate", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(16, toItem(Material.DIAMOND_LEGGINGS, "&aProtection Leggings", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(17, toItem(Material.DIAMOND_BOOTS, "&aProtection Boots", Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(18, toItem(Material.DIAMOND_HELMET, "&cBlast-Prot Helmet", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(19, toItem(Material.DIAMOND_CHESTPLATE, "&cBlast-Prot Chestplate", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(20, toItem(Material.DIAMOND_LEGGINGS, "&cBlast-Prot Leggings", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(21, toItem(Material.DIAMOND_BOOTS, "&cBlast-Prot Boots", Enchantment.PROTECTION_EXPLOSIONS, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(22, toItem(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, Enchantment.DURABILITY, Enchantment.SWEEPING_EDGE, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(23, toItem(Material.DIAMOND_PICKAXE, Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.VANISHING_CURSE));
        put(24, toItem(Material.BOW, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_FIRE, Enchantment.MENDING, Enchantment.ARROW_INFINITE, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE));
        put(25, toItem(Material.TIPPED_ARROW));
        put(26, gen32kShulker());
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
        ItemStack item = toItem(material, enchantments);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.translateChars(name));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack toItem(Material material, Enchantment... enchantments) {
        ItemStack item = new ItemStack(material, material.getMaxStackSize());
        ItemMeta meta = item.getItemMeta();
        for (Enchantment enchantment : enchantments) {
            meta.addEnchant(enchantment, enchantment.getMaxLevel(), true);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack toItem(Material material, short durability) {
        return new ItemStack(material, material.getMaxStackSize(), durability);
    }

    public static ItemStack gen32kShulker() {
        NBTTagCompound blockEntityTag = new NBTTagCompound();
        NBTTagList itemListTag = getNbtTagList();
        blockEntityTag.set("Items", itemListTag);

        net.minecraft.server.v1_12_R1.ItemStack shulkerItem = new net.minecraft.server.v1_12_R1.ItemStack(Blocks.WHITE_SHULKER_BOX);
        NBTTagCompound shulkerTag = new NBTTagCompound();
        shulkerTag.set("BlockEntityTag", blockEntityTag);
        NBTTagCompound displayTag = new NBTTagCompound();
        displayTag.setString("Name", "32ks");
        shulkerTag.set("display", displayTag);
        shulkerItem.setTag(shulkerTag);

        return CraftItemStack.asBukkitCopy(shulkerItem);
    }

    @NotNull
    private static NBTTagList getNbtTagList() {
        NBTTagList itemListTag = new NBTTagList();
        for (int i = 0; i < 27; i++) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            itemTag.setString("id", "minecraft:diamond_sword");
            itemTag.setByte("Count", (byte) 1);
            itemTag.setShort("Damage", (short) 0);

            // enchants
            NBTTagCompound extraTag = new NBTTagCompound();
            NBTTagList enchTag = new NBTTagList();
            NBTTagCompound sharpTag = new NBTTagCompound();
            sharpTag.setShort("id", (short) 16);
            sharpTag.setShort("lvl", (short) 32767);
            enchTag.add(sharpTag);
            extraTag.set("ench", enchTag);
            itemTag.set("tag", extraTag);

            itemListTag.add(itemTag);
        }
        return itemListTag;
    }

    public static ItemStack genButton(int index, boolean next) {
        net.minecraft.server.v1_12_R1.ItemStack item = new net.minecraft.server.v1_12_R1.ItemStack(Blocks.STONE_BUTTON);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInt("next_item_index", index);
        NBTTagCompound display = new NBTTagCompound();
        display.setString("Name", Utils.translateChars(next ? "&aNext" : "&cBack"));
        compound.set("display", display);
        item.setTag(compound);
        return CraftItemStack.asBukkitCopy(item);
    }

    public static ItemStack genSetter(String tagName) {
        net.minecraft.server.v1_12_R1.ItemStack stick = new net.minecraft.server.v1_12_R1.ItemStack(Items.STICK);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean(tagName, true);
        NBTTagCompound display = new NBTTagCompound();
        display.setString("Name", Utils.translateChars("&e&l" + tagName));
        compound.set("display", display);
        stick.setTag(compound);
        return CraftItemStack.asBukkitCopy(stick);
    }
}
