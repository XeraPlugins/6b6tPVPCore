package me.ian.lobby.npc;

import me.ian.PVPHelper;
import me.ian.lobby.npc.custom.ItemVendor;
import me.ian.utils.ItemUtils;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SevJ6
 */
public class NPCManager implements Listener {

    private final List<NPC> npcs = new ArrayList<>();

    public NPCManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, PVPHelper.INSTANCE);

        // TESTING
    }

    public void addNPC(NPC npc) {
        // check to make sure no NPCs having duplicate names
        if (npcs.stream().anyMatch(existingNPC -> existingNPC.getName().equals(npc.getName()))) return;
        npcs.add(npc);
        npc.spawn();
    }

    public NPC getNPC(String name) {
        return npcs.stream().filter(npc -> npc.getName().equals(name)).findAny().orElse(null);
    }

    public void removeNPC(String name) {
        NPC npc = getNPC(name);
        if (npc == null) return;
        npcs.remove(npc);
        npc.remove();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        npcs.forEach(npc -> npc.show(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        npcs.forEach(npc -> {
            if (!npc.isFacePlayers()) return;
            if (npc.getEntityPlayer().getWorld() != ((CraftPlayer) player).getHandle().getWorld()) return;
            if (npc.getLocation().distance(player.getLocation()) < 30) {
                npc.lookAtPlayer(player);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            EntityPlayer ep = Utils.getHandle((Player) event.getRightClicked());
            npcs.stream().filter(npc -> npc.getEntityPlayer().equals(ep)).findAny().ifPresent(npc -> npc.onInteract(event.getPlayer()));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player.hasMetadata("vendor_gui")) {
            Inventory inventory = event.getClickedInventory();
            if (inventory != event.getView().getTopInventory()) return;
            if (inventory.getItem(event.getSlot()) == null) return;
            if (inventory.getItem(event.getSlot()).getType() == Material.STONE_BUTTON) {
                ItemStack button = CraftItemStack.asNMSCopy(inventory.getItem(event.getSlot()));
                if (button.hasTag() && button.getTag() != null) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 10f, 1f);
                    int index = button.getTag().getInt("next_item_index");
                    if (index > ItemUtils.ITEM_INDEX.size()) return;

                    org.bukkit.inventory.ItemStack bukkitStack = ItemUtils.ITEM_INDEX.get(index);

                    switch (bukkitStack.getType()) {
                        case TIPPED_ARROW:
                            PotionMeta meta = (PotionMeta) bukkitStack.getItemMeta();
                            meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
                            bukkitStack.setItemMeta(meta);
                            for (int i = 0; i < 9; i++) {
                                inventory.setItem(i, bukkitStack);
                            }

                            meta.setBasePotionData(new PotionData(PotionType.SPEED, false, true));
                            bukkitStack.setItemMeta(meta);
                            for (int i = 9; i < 18; i++) {
                                inventory.setItem(i, bukkitStack);
                            }

                            meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, true));
                            bukkitStack.setItemMeta(meta);
                            for (int i = 18; i < 27; i++) {
                                inventory.setItem(i, bukkitStack);
                            }

                            // normal arrows
                            org.bukkit.inventory.ItemStack arrow = new org.bukkit.inventory.ItemStack(Material.ARROW, 64);
                            for (int i = 27; i < 36; i++) {
                                inventory.setItem(i, arrow);
                            }

                            break;

                        default:
                            for (int i = 0; i < inventory.getSize(); i++) {
                                inventory.setItem(i, bukkitStack);
                            }
                            break;
                    }

                    ItemVendor vendor = (ItemVendor) player.getMetadata("vendor_gui").get(0).value();
                    if (index > 1) inventory.setItem(27, vendor.genButton(index - 1, false));
                    inventory.setItem(35, vendor.genButton(index + 1, true));
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getReason() == InventoryCloseEvent.Reason.PLUGIN) return;
        if (player.hasMetadata("vendor_gui")) player.removeMetadata("vendor_gui", PVPHelper.INSTANCE);
    }
}
