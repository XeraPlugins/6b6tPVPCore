package me.ian.lobby.npc.custom;

import me.ian.lobby.npc.NPC;
import me.ian.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SevJ6
 */
public class ItemVendor extends NPC {

    private static ItemStack toItem(Material material) {
        return new ItemStack(material, material.getMaxStackSize());
    }

    public ItemVendor(Location location, String name, SkinTexture texture, boolean shouldFacePlayers) {
        super(location, name, texture, shouldFacePlayers);
    }

    @Override
    public void onInteract(Player player) {
        player.openInventory(genCashierInventory());
    }

    public Inventory genCashierInventory() {
        Inventory inventory = Bukkit.createInventory(getEntityPlayer().getBukkitEntity(), 36, getName());
        ItemUtils.ITEM_INDEX.forEach(inventory::setItem);
        return inventory;
    }
}
