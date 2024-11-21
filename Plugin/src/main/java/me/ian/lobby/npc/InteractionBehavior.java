package me.ian.lobby.npc;

import me.ian.PVPHelper;
import me.ian.arena.ArenaManager;
import me.ian.kits.Kit;
import me.ian.kits.KitManager;
import me.ian.utils.ItemUtils;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public enum InteractionBehavior {

    ITEM_VENDOR {
        @Override
        public void execute(Player player, NPC npc) {
            player.setMetadata("vendor_gui", new FixedMetadataValue(PVPHelper.INSTANCE, npc));
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

            // Create Vendor Inventory
            Inventory inventory = Bukkit.createInventory(npc.getEntityPlayer().getBukkitEntity(), 36, npc.getName());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, ItemUtils.ITEM_INDEX.get(1));
            }

            inventory.setItem(35, ItemUtils.genButton(2, true));
            player.openInventory(inventory);
        }
    },

    GIVE_RANDOM_KIT {
        @Override
        public void execute(Player player, NPC npc) {
            List<Kit> globalKits = PVPHelper.INSTANCE.getKitManager().getGlobalKits();
            Optional.ofNullable(globalKits.get(ThreadLocalRandom.current().nextInt(0, globalKits.size()))).ifPresent(kit -> {
                kit.equip(player);
                Utils.sendMessage(player, String.format("<%s> I just gave you a random kit!", npc.getName()));
            });
        }
    },

    SEND_TO_ARENA {
        @Override
        public void execute(Player player, NPC npc) {
            NBTTagCompound compound = npc.getData();
            if (compound.hasKey("arena_endpoint")) {
                Optional.ofNullable(PVPHelper.INSTANCE.getArenaManager().getArena(compound.getString("arena_endpoint"))).ifPresent(arena -> {
                    Location location = arena.getRandomLocation();
                    player.teleport(location);
                });
            }
        }
    };

    public abstract void execute(Player player, NPC npc);
}
