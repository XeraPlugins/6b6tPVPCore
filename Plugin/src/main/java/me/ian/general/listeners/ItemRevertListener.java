package me.ian.general.listeners;

import me.ian.PVPHelper;
import me.ian.utils.ItemUtils;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class ItemRevertListener extends ItemUtils implements Listener {

    public ItemRevertListener() {
        PVPHelper.EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            Utils.run(() -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    InventoryView view = player.getOpenInventory();
                    if (view != null) {
                        revertInventory(view.getTopInventory());
                        revertInventory(view.getBottomInventory());
                    } else {
                        revertInventory(player.getInventory());
                    }
                });
            });
        }, 0L, 2500L, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (isIllegal(event.getItem().getItemStack())) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isIllegal(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            revertInventory(event.getPlayer().getInventory());
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        if (event.getItem() != null && isIllegal(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if (event.getEntity() instanceof Item) {
            ItemStack item = ((Item) event.getEntity()).getItemStack();
            if (isIllegal(item)) {
                event.setCancelled(true);
                event.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (isIllegal(event.getMainHandItem())) {
            revertItemStack(event.getMainHandItem());
            System.out.println("reverted, swap item event");
        }
        else if (isIllegal(event.getOffHandItem())) {
            revertItemStack(event.getOffHandItem());
            System.out.println("reverted, swap item event");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof ShulkerBox) {
            ShulkerBox box = (ShulkerBox) event.getBlockPlaced().getState();
            revertInventory(box.getInventory());
        }
    }
}
