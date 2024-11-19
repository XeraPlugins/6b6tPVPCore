package me.ian.lobby.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.ian.PVPHelper;
import me.ian.lobby.npc.custom.ItemVendor;
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
import java.util.List;
import java.util.UUID;

/**
 * @author SevJ6
 */
@Getter
@Setter
public abstract class NPC {

    private Location location;
    private String name;
    private GameProfile gameProfile;
    private EntityPlayer entityPlayer;
    private SkinTexture texture;
    private boolean facePlayers;
    private final NBTTagCompound data;

    public NPC(Location location, String name, SkinTexture texture, boolean shouldFacePlayers) {
        this.location = location;
        this.name = name;
        this.texture = texture;
        this.facePlayers = shouldFacePlayers;
        this.data = getNbtData();
    }

    public void spawn() {
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', name));
        gameProfile.getProperties().put("textures", new Property("textures", texture.getTexture(), texture.getSignature()));
        entityPlayer = new EntityPlayer(server, worldServer, gameProfile, new PlayerInteractManager(worldServer));
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f);
        entityPlayer.playerConnection = new PlayerConnection(server, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), entityPlayer);
        entityPlayer.setInvulnerable(true);
        worldServer.addEntity(entityPlayer);
        Bukkit.getOnlinePlayers().stream().filter(player -> player != entityPlayer.getBukkitEntity()).forEach(this::show);
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

    public void setNameInvisible() {
        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), entityPlayer.getName());
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        List<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(entityPlayer.getName());
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
            connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
            connection.sendPacket(new PacketPlayOutScoreboardTeam(team, playerToAdd, 3));
        });
    }

    public void remove() {
        Bukkit.getOnlinePlayers().forEach(this::disappear);
        entityPlayer.die();
    }

    public void disappear(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        connection.sendPacket(destroyPacket);
    }

    public void show(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((location.getYaw() * 256f) / 360f)));

        // https://hypixel.net/threads/npc-player-skin-help.1440574/
        DataWatcher watcher = entityPlayer.getDataWatcher();
        entityPlayer.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true));

        // remove from tablist afterwards
        Bukkit.getScheduler().runTaskLater(PVPHelper.INSTANCE, () -> {
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        }, 10L);
    }

    public abstract void onInteract(Player player);


    private NBTTagCompound getNbtData() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Name", this.name);
        NBTTagCompound skinTag = new NBTTagCompound();
        skinTag.setString("texture", texture.getTexture());
        skinTag.setString("signature", texture.getSignature());
        compound.set("Skin", skinTag);
        compound.setBoolean("FacePlayers", this.facePlayers);
        NBTUtils.writeLocationToTag(compound, this.location);
        compound.setBoolean("ItemVendor", this instanceof ItemVendor);
        return compound;
    }

    @Data
    @AllArgsConstructor
    public static class SkinTexture {
        private String texture;
        private String signature;
    }
}