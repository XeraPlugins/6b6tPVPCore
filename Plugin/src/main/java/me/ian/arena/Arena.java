package me.ian.arena;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Getter;
import lombok.SneakyThrows;
import me.ian.PVPHelper;
import me.ian.utils.area.BoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Arena {

    private final String name;
    private final World world;
    private final BoundingBox boundingBox;
    private final boolean isDuelArena;

    public Arena(String name, BoundingBox boundingBox, boolean isDuelArena) {
        this.name = name;
        this.boundingBox = boundingBox;
        this.world = boundingBox.getWorld();
        this.isDuelArena = isDuelArena;
        if (isDuelArena) PVPHelper.INSTANCE.getDuelManager().getDuelArenas().add(this);
    }


    // Check if a location is within the bounds of the arena
    public boolean isLocationWithinBounds(Location loc) {
        return boundingBox.isLocationWithinBounds(loc);
    }

    // Check if a player is within the bounds of the arena
    public boolean isPlayerWithinBounds(Player player) {
        return boundingBox.isPlayerWithinBounds(player);
    }

    // Get all players in the arena
    public List<Player> getPlayers() {
        return boundingBox.getPlayers();
    }

    // Get all non-player entities in an arena
    public List<Entity> getEntities() {
        return boundingBox.getEntities();
    }

    public Location getRandomLocation() {
        Random random = new Random();
        Location pointA = boundingBox.getPointA();
        Location pointB = boundingBox.getPointB();

        // Calculate the center of the bounding box
        double centerX = (pointA.getX() + pointB.getX()) / 2;
        double centerZ = (pointA.getZ() + pointB.getZ()) / 2;

        // Get the half-lengths for each axis
        double halfLengthX = Math.abs(pointA.getX() - pointB.getX()) / 2.0;
        double halfLengthZ = Math.abs(pointA.getZ() - pointB.getZ()) / 2.0;

        // Generate random coordinates within the arena's bounds
        Location randomLocation;
        do {
            double randomX = centerX + (random.nextDouble() * 2 - 1) * halfLengthX;
            double randomZ = centerZ + (random.nextDouble() * 2 - 1) * halfLengthZ;

            randomLocation = getHighestSpot(new Location(getWorld(), randomX, -1, randomZ));
        } while (!randomLocation.getNearbyPlayers(6).isEmpty());

        return randomLocation;
    }

    // Override bukkit's World.getHighestBlockAt method. Not sure why but it just fucks up sometimes
    public Location getHighestSpot(Location location) {
        Location clone = location.clone();
        for (double y = Math.max(boundingBox.getPointA().getY(), boundingBox.getPointB().getY()) - 1; y > 0; y--) {
            clone.setY(y);
            if (!clone.getBlock().isEmpty()) break;
        }
        return clone.add(0.0, 1.0, 0.0);
    }

    @SneakyThrows
    public void clear() {
        Location pointA = boundingBox.getPointA();
        Location pointB = boundingBox.getPointB();
        CuboidRegion region = new CuboidRegion(BukkitUtil.getLocalWorld(getWorld()), new Vector(pointA.getX(), pointA.getY(), pointA.getZ()), new Vector(pointB.getX(), pointB.getY(), pointB.getZ()));
        EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(getWorld()), region.getArea());
        for (int x = region.getMinimumPoint().getBlockX(); x <= region.getMaximumPoint().getBlockX(); x++) {
            for (int y = region.getMinimumPoint().getBlockY(); y <= region.getMaximumPoint().getBlockY(); y++) {
                for (int z = region.getMinimumPoint().getBlockZ(); z <= region.getMaximumPoint().getBlockZ(); z++) {
                    if (getWorld().getBlockAt(x, y, z).getType() != Material.BEDROCK) {
                        session.setBlock(new Vector(x, y, z), new BaseBlock(0));
                    }
                }
            }
        }
        Operations.complete(session.commit());
        session.flushQueue();
        getEntities().forEach(Entity::remove); // remove all entities
    }

}
