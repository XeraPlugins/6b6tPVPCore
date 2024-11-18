package me.ian.kits.gui.listener;

import lombok.AllArgsConstructor;
import me.ian.PVPHelper;
import me.ian.kits.KitManager;
import me.ian.kits.gui.KitGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

@AllArgsConstructor
public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player.hasMetadata("kit_gui")) {
            KitGui gui = (KitGui) player.getMetadata("kit_gui").get(0).value();
            if (gui != null) gui.onSlotClick(event.getSlot());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getReason() == InventoryCloseEvent.Reason.PLUGIN) return;
        if (player.hasMetadata("kit_gui")) player.removeMetadata("kit_gui", PVPHelper.INSTANCE);
    }
}
