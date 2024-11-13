package me.ian.event.listeners;

import me.ian.PVPHelper;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.TileEntityBed;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;

public class BedPlacementListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!checkValidity(event)) return;
        Player player = event.getPlayer();
        BlockFace blockFace = event.getBlockFace();
        Location location = event.getClickedBlock().getLocation();
        ItemStack handItem = (event.getHand() == EquipmentSlot.HAND) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
        Block blockInteractedWith = location.getBlock();
        short bedColor = getColor(handItem);
        Block blockToSet = blockInteractedWith.getRelative(blockFace);
        handlePlace(player, blockToSet, handItem, bedColor);
    }

    public boolean checkValidity(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = (event.getHand() == EquipmentSlot.HAND) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
        if (handItem != null) {
            Block block = event.getClickedBlock();
            return PVPHelper.INSTANCE.getArenaManager().isPlayerInArena(player) &&
                    handItem.getType() == Material.BED &&
                    block != null &&
                    event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    block.getType() != Material.BED_BLOCK;
        }
        return false;
    }

    public void handlePlace(Player player, Block block, ItemStack itemStack, short color) {
        if (block == null) return;
       Utils.run(() -> {
            boolean place = setBed(block, getFacing(player), color, player);
            if (place) {
                player.playSound(player.getLocation(), Sound.BLOCK_WOOD_PLACE, 1.2F, 0.8F);
                if (player.getGameMode() == GameMode.SURVIVAL) itemStack.subtract();
            }
        });
    }

    public short getColor(ItemStack handItem) {
        short metadata = 0;
        if (handItem.getType() == Material.BED) {
            metadata = handItem.getDurability();
        }
        return metadata;
    }

    public void changeBedColor(TileEntityBed bed, short color) {
        if (bed != null) {
            NBTTagCompound tagCompound = bed.d();
            tagCompound.setShort("color", color);
            bed.load(tagCompound);
            bed.save(tagCompound);
        }
    }

    public BlockFace getFacing(Player player) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        BlockFace face = null;
        switch (p.getDirection()) {
            case NORTH:
                face = BlockFace.NORTH;
                break;
            case EAST:
                face = BlockFace.EAST;
                break;
            case SOUTH:
                face = BlockFace.SOUTH;
                break;
            case WEST:
                face = BlockFace.WEST;
                break;
        }
        return face;
    }

    public boolean setBed(Block bedFoot, BlockFace facing, short color, Player player) {

        net.minecraft.server.v1_12_R1.World world = ((CraftWorld) bedFoot.getWorld()).getHandle();
        BlockState footState = bedFoot.getState();
        BlockState headState = bedFoot.getRelative(facing).getState();

        if (!footState.getType().equals(Material.AIR) || !headState.getType().equals(Material.AIR)
                || footState.getType() == Material.BED_BLOCK || headState.getType() == Material.BED_BLOCK)
            return false;

        // handle foot of bed
        footState.setType(Material.BED_BLOCK);
        Bed footData = new Bed(Material.BED_BLOCK);
        footData.setFacingDirection(facing);
        footData.setHeadOfBed(false);
        footState.setData(footData);
        footState.update(true, false);
        BlockPosition footPosition = new BlockPosition(bedFoot.getX(), bedFoot.getY(), bedFoot.getZ());
        TileEntityBed bedFootTile = (TileEntityBed) world.getTileEntity(footPosition);
        changeBedColor(bedFootTile, color);

        // handle head of bed
        headState.setType(Material.BED_BLOCK);
        Bed headData = new Bed(Material.BED_BLOCK);
        headData.setFacingDirection(facing);
        headData.setHeadOfBed(true);
        headState.setData(headData);
        headState.update(true, false);
        BlockPosition headPosition = new BlockPosition(headState.getX(), headState.getY(), headState.getZ());
        TileEntityBed bedHeadTile = (TileEntityBed) world.getTileEntity(headPosition);
        changeBedColor(bedHeadTile, color);

        return true;
    }
}
