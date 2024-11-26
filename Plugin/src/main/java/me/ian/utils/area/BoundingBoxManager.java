package me.ian.utils.area;

import lombok.Data;
import me.ian.PVPHelper;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@Data
public class BoundingBoxManager implements Listener {

    private Location globalPointA;
    private Location globalPointB;
    private Location globalPointC;

    public BoundingBoxManager() {
        PVPHelper.INSTANCE.registerListener(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        ItemStack nmsItem = CraftItemStack.asNMSCopy(event.getItem());
        Player player = event.getPlayer();
        if (nmsItem.getTag() == null) return;
        if (event.getClickedBlock() == null) return;
        Location clickedLocation = event.getClickedBlock().getLocation();
        if (nmsItem.getTag().hasKey("boundBoxSetter")) {
            event.setCancelled(true);
            switch (event.getAction()) {
                case LEFT_CLICK_BLOCK: // set point A
                    globalPointA = clickedLocation;
                    Utils.sendMessage(player, String.format("&bSet position 1 at %s, %s, %s", globalPointA.getX(), globalPointA.getY(), globalPointA.getZ()));
                    break;

                case RIGHT_CLICK_BLOCK: // set point B
                    globalPointB = clickedLocation;
                    Utils.sendMessage(player, String.format("&bSet position 2 at %s, %s, %s", globalPointB.getX(), globalPointB.getY(), globalPointB.getZ()));
                    break;
            }
        } else if (nmsItem.getTag().hasKey("exitPortalSetter") && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            globalPointC = clickedLocation.add(0.5, 1, 0.5);
            Utils.sendMessage(player, String.format("&bSet exit portal position at %s, %s, %s", globalPointC.getX(), globalPointC.getY(), globalPointC.getZ()));
        }
    }
}
