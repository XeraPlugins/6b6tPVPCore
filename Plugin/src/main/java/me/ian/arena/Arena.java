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
import me.ian.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class Arena {

    private final String name;
    private final World world;
    private final Location pointA;
    private final Location pointB;
    private final boolean isDuelArena;

    public Arena(String name, World world, Location pointA, Location pointB, boolean isDuelArena) {
        this.name = name;
        this.world = world;
        this.pointA = pointA;
        this.pointB = pointB;
        this.isDuelArena = isDuelArena;
        if (isDuelArena) PVPHelper.INSTANCE.getDuelManager().getDuelArenas().add(this);
    }


    // Check if a location is within the bounds of the arena
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

    // Check if a player is within the bounds of the arena
    public boolean isPlayerWithinBounds(Player player) {
        return isLocationWithinBounds(player.getLocation());
    }

    public List<Player> getPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> Objects.equals(world, player.getWorld()))
                .filter(this::isPlayerWithinBounds)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntities() {
        return getWorld().getEntities().stream()
                .filter(entity -> !(entity instanceof Player))
                .filter(entity -> isLocationWithinBounds(entity.getLocation()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public void clear() {
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
