package me.ian.lobby.npc.custom;

import me.ian.lobby.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author SevJ6
 */
public class Cashier extends NPC {
    public Cashier(Location location, String name, SkinTexture texture, boolean shouldFacePlayers) {
        super(location, name, texture, shouldFacePlayers);
    }

    @Override
    public void onInteract(Player player) {
        player.openInventory(genCashierInventory());
    }

    public Inventory genCashierInventory() {
        Inventory inventory = Bukkit.createInventory(getEntityPlayer().getBukkitEntity(), 27, getName());
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemStack(Material.END_CRYSTAL, 64));
        }

        return inventory;
    }
}
