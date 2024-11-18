package me.ian.lobby.npc.custom;

import me.ian.PVPHelper;
import me.ian.lobby.npc.NPC;
import me.ian.utils.ItemUtils;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author SevJ6
 */
public class ItemVendor extends NPC {

    private static ItemStack toItem(Material material) {
        return new ItemStack(material, material.getMaxStackSize());
    }

    public ItemVendor(Location location, String name, SkinTexture texture, boolean shouldFacePlayers) {
        super(location, name, texture, shouldFacePlayers);
    }

    @Override
    public void onInteract(Player player) {
        player.setMetadata("vendor_gui", new FixedMetadataValue(PVPHelper.INSTANCE, this));
        player.openInventory(genInitialInventory());
    }

    public Inventory genInitialInventory() {
        Inventory inventory = Bukkit.createInventory(getEntityPlayer().getBukkitEntity(), 36, getName());
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, ItemUtils.ITEM_INDEX.get(1));
        }

        inventory.setItem(35, genButton(2, true));
        return inventory;
    }

    public Inventory genInventory(int index) {
        Inventory inventory = Bukkit.createInventory(getEntityPlayer().getBukkitEntity(), 36, getName());
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, ItemUtils.ITEM_INDEX.get(index));
        }

        inventory.setItem(27, genButton(index - 1, false));
        inventory.setItem(35, genButton(index + 1, true));
        return inventory;
    }

    private ItemStack genButton(int index, boolean next) {
        net.minecraft.server.v1_12_R1.ItemStack item = new net.minecraft.server.v1_12_R1.ItemStack(Blocks.STONE_BUTTON);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInt("next_item_index", index);
        NBTTagCompound display = new NBTTagCompound();
        display.setString("Name", Utils.translateChars(next ? "&aNext" : "&cBack"));
        compound.set("display", display);
        item.setTag(compound);
        return CraftItemStack.asBukkitCopy(item);
    }
}
