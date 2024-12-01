package me.ian.general.listeners;

import me.ian.PVPHelper;
import me.ian.mixin.event.ItemCreateEvent;
import me.ian.utils.ItemUtils;
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
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
        }, 0L, 3000L, TimeUnit.MILLISECONDS);
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
        }
        else if (isIllegal(event.getOffHandItem())) {
            revertItemStack(event.getOffHandItem());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof ShulkerBox) {
            ShulkerBox box = (ShulkerBox) event.getBlockPlaced().getState();
            revertInventory(box.getInventory());
        }
    }

    @EventHandler
    public void onCreate(InventoryMoveItemEvent event) {
        if (event.getDestination().getLocation().getWorld().getEnvironment() != World.Environment.NETHER) {
            if (isIllegal(event.getItem())) {
                event.getItem().setAmount(-1);
                event.getDestination().getLocation().getBlock().setType(Material.AIR);
                event.getDestination().getViewers().forEach(entity -> entity.sendMessage(ChatColor.RED + "32ks are not enabled in this area."));
            }
        }
    }
}
