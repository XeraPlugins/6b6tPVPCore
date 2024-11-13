package me.ian.event.listeners;

import me.ian.utils.ItemUtils;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class ItemRevertListener extends ItemUtils implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onOpen(InventoryOpenEvent event) {
        revertInventory(event.getInventory());
        revertInventory(event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClose(InventoryCloseEvent event) {
        revertInventory(event.getInventory());
        revertInventory(event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (isIllegal(event.getItem().getItemStack())) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDrop(PlayerDropItemEvent event) {
        if (isIllegal(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            revertInventory(event.getPlayer().getInventory());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDispense(BlockDispenseEvent event) {
        if (event.getItem() != null && isIllegal(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPortal(EntityPortalEvent event) {
        if (event.getEntity() instanceof Item) {
            ItemStack item = ((Item) event.getEntity()).getItemStack();
            if (isIllegal(item)) {
                event.setCancelled(true);
                event.getEntity().remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (isIllegal(event.getMainHandItem())) revertItemStack(event.getMainHandItem());
        else if (isIllegal(event.getOffHandItem())) revertItemStack(event.getOffHandItem());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof ShulkerBox) {
            ShulkerBox box = (ShulkerBox) event.getBlockPlaced().getState();
            revertInventory(box.getInventory());
        }
    }
}
