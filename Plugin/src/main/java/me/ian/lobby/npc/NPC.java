package me.ian.lobby.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
public class NPC {

    private Location location;
    private String name;
    private GameProfile gameProfile;
    private EntityPlayer entityPlayer;
    private String texture;
    private String signature;
    private boolean facePlayers;

    public NPC(Location location, String name, String texture, String signature, boolean shouldFacePlayers) {
        this.location = location;
        this.name = name;
        this.texture = texture;
        this.signature = signature;
        this.facePlayers = shouldFacePlayers;
    }

    public void spawn() {
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', name));
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        entityPlayer = new EntityPlayer(server, worldServer, gameProfile, new PlayerInteractManager(worldServer));
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f);
        entityPlayer.playerConnection = new PlayerConnection(server, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), entityPlayer);
        worldServer.addEntity(entityPlayer);
        Bukkit.getOnlinePlayers().forEach(this::show);
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
//        entityPlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(), yawAngle, pitchAngle);
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
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((location.getYaw() * 256f) / 360f)));

        // https://hypixel.net/threads/npc-player-skin-help.1440574/
        DataWatcher watcher = entityPlayer.getDataWatcher();
        entityPlayer.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true));
    }

    public void onInteract(Player player) {

    }
}