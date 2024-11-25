package me.ian.general.listeners;

import me.ian.utils.Utils;
import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEnderCrystal;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;

public class FastCrystalListener implements PacketListener {
    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        Player player = event.getPlayer();
        if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER)) return; // Only allow this in the nether

        PacketPlayInUseItem packet = (PacketPlayInUseItem) event.getPacket();
        BlockPosition placePos = packet.a();
        if (placePos == null) return;
        Location placeLocation = new Location(player.getWorld(), placePos.getX(), placePos.getY(), placePos.getZ());
        boolean holdingCrystal = (packet.c().equals(EnumHand.OFF_HAND)) ? player.getInventory().getItemInOffHand().getType() == org.bukkit.Material.END_CRYSTAL : player.getInventory().getItemInMainHand().getType() == org.bukkit.Material.END_CRYSTAL;
        org.bukkit.Material typeAtPos = placeLocation.getBlock().getType();
        boolean isAttemptingToCrystal = holdingCrystal && (typeAtPos == org.bukkit.Material.OBSIDIAN || typeAtPos == org.bukkit.Material.BEDROCK);
        if (isAttemptingToCrystal) {
            Location crystalLocation = placeLocation.clone().add(0.5, 1, 0.5);
            EntityEnderCrystal enderCrystal = crystalLocation.getNearbyEntitiesByType(EnderCrystal.class, 0.5, 1, 0.5).stream().map(crystal -> ((CraftEnderCrystal) crystal).getHandle()).findAny().orElse(null);
            if (enderCrystal != null) {
                Utils.run(() -> {
                    enderCrystal.damageEntity(DamageSource.playerAttack(((CraftPlayer) player).getHandle()), 10.0F);
                });
            }
        }
    }
    @Override
    public void outgoing(PacketEvent.Outgoing outgoing) throws Throwable {

    }
}
