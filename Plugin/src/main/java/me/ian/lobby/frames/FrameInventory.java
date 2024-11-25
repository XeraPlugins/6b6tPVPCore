package me.ian.lobby.frames;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class FrameInventory {

    private final Inventory inventory;
    private final String name;
    private final ItemStack item;

    public FrameInventory(String name, ItemStack item) {
        this.name = name;
        this.item = item;
        this.item.setAmount(item.getMaxStackSize());
        this.inventory = Bukkit.createInventory(null, 18, name);
        for (int i = 0; i < inventory.getContents().length; i++) {
            inventory.setItem(i, item);
        }
    }

    public void open(Player player) {
        if (player.getOpenInventory() != null) player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        player.openInventory(inventory);
    }
}
