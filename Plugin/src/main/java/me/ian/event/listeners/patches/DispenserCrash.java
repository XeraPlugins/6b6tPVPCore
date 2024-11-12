package me.ian.event.listeners.patches;

import me.ian.PVPHelper;
import org.bukkit.Location;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.logging.Level;

public class DispenserCrash implements Listener {

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        if (event.getBlock().getState() instanceof Dropper) return;
        int height = event.getBlock().getY();
        Dispenser dispenser = (Dispenser) event.getBlock().getState();
        if (!hasShulker(dispenser)) return;
        if (height == 255 || height <= 1) {
            event.setCancelled(true);
            Location location = dispenser.getLocation();
            PVPHelper.INSTANCE.getLogger().log(Level.INFO, String.format("[Dispenser Crash] Stopped dispenser from dispensing in World: %s, X: %s, Y: %s, Z: %s", location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
        }
    }

    private boolean hasShulker(Dispenser dispenser) {
        for (ItemStack item : dispenser.getInventory()) {
            if (item != null && isShulker(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isShulker(ItemStack item) {
        return item.hasItemMeta()
                && item.getItemMeta() instanceof BlockStateMeta
                && ((BlockStateMeta) item.getItemMeta()).getBlockState() instanceof ShulkerBox;
    }
}
