package me.ian.lobby.npc;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.util.internal.ConcurrentSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.ian.utils.NBTUtils;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author SevJ6
 */
@Getter
@Setter
public class NPC {

    private Location location;
    private String name;
    private GameProfile gameProfile;
    private EntityPlayer entityPlayer;
    private SkinTexture texture;
    private boolean facePlayers;
    private NBTTagCompound data;
    private InteractionBehavior behavior;
    private ConcurrentSet<Player> playersInRange;

    public Function<EntityPlayer, List<Packet<?>>> getSpawnPackets = (entityPlayer) -> {
        DataWatcher watcher = entityPlayer.getDataWatcher();
        entityPlayer.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);

        return Arrays.asList(new Packet<?>[]{
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer),
                new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true),
                new PacketPlayOutNamedEntitySpawn(entityPlayer)
        });
    };

    public NPC(Location location, String name, SkinTexture texture, boolean shouldFacePlayers, InteractionBehavior behavior) {
        this.location = location;
        this.name = name;
        this.texture = texture;
        this.facePlayers = shouldFacePlayers;
        this.behavior = behavior;
        this.data = getNbtData();
        this.playersInRange = new ConcurrentSet<>();
    }

    public void spawn() {
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        gameProfile = new GameProfile(UUID.nameUUIDFromBytes(("NPC:" + name).getBytes(Charsets.UTF_8)), ChatColor.translateAlternateColorCodes('&', name));
        gameProfile.getProperties().put("textures", new Property("textures", texture.getTexture(), texture.getSignature()));
        entityPlayer = new EntityPlayer(server, worldServer, gameProfile, new PlayerInteractManager(worldServer));
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f);
        entityPlayer.playerConnection = new PlayerConnection(server, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), entityPlayer);
        worldServer.addEntity(entityPlayer);
    }

    public void lookAtPlayer(Player player) {
        location.setDirection(player.getLocation().subtract(location).toVector());
        byte yawAngle = (byte) ((location.getYaw() % 360) * 256 / 360);
        byte pitchAngle = (byte) ((location.getPitch() % 360) * 256 / 360);
        PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(entityPlayer, yawAngle);
        PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), yawAngle, pitchAngle, entityPlayer.onGround);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(headRotationPacket);
        connection.sendPacket(lookPacket);
    }

    public void despawn() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
            connection.sendPacket(destroyPacket);
        });
        entityPlayer.die();
    }

    public void show(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        getSpawnPackets.apply(entityPlayer).forEach(connection::sendPacket);
    }

    public void onInteract(Player player) {
        behavior.execute(player, this);
    }

    private NBTTagCompound getNbtData() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Name", this.name);
        NBTTagCompound skinTag = new NBTTagCompound();
        skinTag.setString("texture", texture.getTexture());
        skinTag.setString("signature", texture.getSignature());
        compound.set("Skin", skinTag);
        compound.setBoolean("FacePlayers", this.facePlayers);
        NBTUtils.writeLocationToTag(compound, this.location);
        compound.setString("Behavior", behavior.name());
        return compound;
    }

    public double getDistance(Player player) {
        return player.getLocation().distance(getEntityPlayer().getBukkitEntity().getLocation());
    }

    public void onRangeEnter(Player player) {
        show(player);
    }

    @Data
    @AllArgsConstructor
    public static class SkinTexture {
        private String texture;
        private String signature;
    }
}