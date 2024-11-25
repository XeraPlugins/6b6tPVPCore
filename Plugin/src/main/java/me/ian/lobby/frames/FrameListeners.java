package me.ian.lobby.frames;

import me.ian.PVPHelper;
import me.ian.lobby.frames.event.PlaceItemInFrameEvent;
import me.ian.utils.Utils;
import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class FrameListeners implements Listener, PacketListener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        Player player = event.getPlayer();
        if (PVPHelper.INSTANCE.getArenaManager().isPlayerInArena(player)) return;
        ItemFrame frame = (ItemFrame) event.getRightClicked();
        if (frame.getItem() == null || frame.getItem().getType().equals(org.bukkit.Material.AIR)) return;
        frame.setRotation(Rotation.COUNTER_CLOCKWISE_45);
        FrameInventory frameInventory = new FrameInventory(Utils.translateChars(Utils.getPrefix() + "&1" + frame.getItem().getI18NDisplayName()), frame.getItem());
        frameInventory.open(player);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (!PVPHelper.INSTANCE.getArenaManager().isLocationInArena(event.getEntity().getLocation())) event.setCancelled(true);
    }

    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent event) {
        event.setCancelled(!event.getPlayer().isOp());
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        event.setCancelled(!event.getRemover().isOp());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        event.setCancelled(event.getEntity() instanceof ItemFrame);
    }

    @EventHandler
    public void onItemFramePlace(PlaceItemInFrameEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
            Location location = event.getFrame().getLocation();
            World world = ((CraftWorld) event.getFrame().getWorld()).getHandle();
            EntityItemFrame entityItemFrame = new EntityItemFrame(world, new BlockPosition(location.getX(), location.getY(), location.getZ()), ((CraftItemFrame) event.getFrame()).getHandle().direction);
            event.getFrame().remove();
            entityItemFrame.setItem(new ItemStack(Item.getById(0)));
            Utils.run(() -> {
                world.addEntity(entityItemFrame);
            });
        }
    }

    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) event.getPacket();
        if (packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT)) {
            World world = ((CraftWorld) event.getPlayer().getWorld()).getHandle();
            Entity entity = packet.a(world);
            if (entity instanceof EntityItemFrame) {
                ItemFrame frame = (ItemFrame) entity.getBukkitEntity();
                EntityItemFrame entityItemFrame = (EntityItemFrame) entity;
                if (!entityItemFrame.getItem().isEmpty()) return;
                org.bukkit.inventory.ItemStack item = (packet.b().equals(EnumHand.OFF_HAND)) ? event.getPlayer().getInventory().getItemInOffHand() : event.getPlayer().getInventory().getItemInMainHand();
                PlaceItemInFrameEvent frameEvent = new PlaceItemInFrameEvent(event.getPlayer(), frame, item);
                Bukkit.getServer().getPluginManager().callEvent(frameEvent);
                if (event.isCancelled()) event.setCancelled(true);
            }
        }
    }

    @Override
    public void outgoing(PacketEvent.Outgoing outgoing) throws Throwable {

    }
}
