package me.ian.kits.gui;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;

@Getter
public abstract class KitGui {


    private final Inventory inventory;
    private final Player player;
    private final int size;
    private final String title;

    public KitGui(Player player, int size, String title, InventoryHolder holder) {
        this.player = player;
        this.size = size;
        this.title = title;
        this.inventory = Bukkit.createInventory(holder, size, Utils.translateChars(title));
        this.player.setMetadata("kit_gui", new FixedMetadataValue(PVPHelper.INSTANCE, this));
    }

    public void open() {
        if (player.getOpenInventory() != null) player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        player.openInventory(inventory);
    }

    public abstract void onSlotClick(int slot);
}
