package me.ian.utils;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class BoundingBox {

    private final World world;
    private final Location pointA;
    private final Location pointB;

    public BoundingBox(World world, Location pointA, Location pointB) {
        if (!world.equals(pointA.getWorld()) || !world.equals(pointB.getWorld())) throw new IllegalArgumentException("Worlds must be the same!");

        this.world = world;
        this.pointA = pointA;
        this.pointB = pointB;
    }

    // Check if a location is within the bounds of the box
    public boolean isLocationWithinBounds(Location loc) {
        double minX = Math.min(pointA.getX(), pointB.getX());
        double maxX = Math.max(pointA.getX(), pointB.getX());
        double minY = Math.min(pointA.getY(), pointB.getY());
        double maxY = Math.max(pointA.getY(), pointB.getY());
        double minZ = Math.min(pointA.getZ(), pointB.getZ());
        double maxZ = Math.max(pointA.getZ(), pointB.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    // Check if a player is within the bounds of the box
    public boolean isPlayerWithinBounds(Player player) {
        Location blockLocation = player.getLocation().getBlock().getLocation();
        return isLocationWithinBounds(blockLocation);
    }

    // Get all players in the box
    public List<Player> getPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> Objects.equals(world, player.getWorld()))
                .filter(this::isPlayerWithinBounds)
                .collect(Collectors.toList());
    }

    // Get all non-player entities in the box
    public List<Entity> getEntities() {
        return getWorld().getEntities().stream()
                .filter(entity -> !(entity instanceof Player))
                .filter(entity -> isLocationWithinBounds(entity.getLocation().getBlock().getLocation()))
                .collect(Collectors.toList());
    }

    public NBTTagCompound toCompound() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("world", world.getName());
        compound.setDouble("x1", pointA.getX());
        compound.setDouble("y1", pointA.getY());
        compound.setDouble("z1", pointA.getZ());
        compound.setDouble("x2", pointB.getX());
        compound.setDouble("y2", pointB.getY());
        compound.setDouble("z2", pointB.getZ());
        return compound;
    }

    public void write(NBTTagCompound compound) {
        compound.set("BoundingBox", toCompound());
    }

    public static BoundingBox read(NBTTagCompound compound) {
        if (!compound.hasKey("BoundingBox")) throw new NullPointerException(compound.toString() + " Compound does not have a BoundingBox tag!");
        
        NBTTagCompound boundingBoxTag = compound.getCompound("BoundingBox");
        // Ensure world is not null
        String worldName = boundingBoxTag.getString("world");
        World w = Bukkit.getWorld(worldName);
        if (w == null) throw new NullPointerException("World " + worldName + " is null!");

        Location pointA = new Location(w, boundingBoxTag.getDouble("x1"), boundingBoxTag.getDouble("y1"), boundingBoxTag.getDouble("z1"));
        Location pointB = new Location(w, boundingBoxTag.getDouble("x2"), boundingBoxTag.getDouble("y2"), boundingBoxTag.getDouble("z2"));

        return new BoundingBox(w, pointA, pointB);
    }
}
