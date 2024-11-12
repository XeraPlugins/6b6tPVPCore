package me.ian.arena;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.utils.NBTUtils;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

@Getter
public class ArenaManager implements Listener {

    private final File arenaDataFolder;
    private final List<Arena> arenas = new ArrayList<>();
    private Location creationPos1;
    private Location creationPos2;

    public ArenaManager() {
        arenaDataFolder = new File(PVPHelper.INSTANCE.getDataFolder(), "arenas");
        if (!arenaDataFolder.exists()) arenaDataFolder.mkdirs();
        Arrays.stream(Objects.requireNonNull(arenaDataFolder.listFiles())).filter(file -> file.getName().endsWith(".nbt")).forEach(file -> {
            NBTTagCompound compound = NBTUtils.readTagFromFile(file);
            Arena arena = fromCompound(compound);
            arenas.add(arena);
        });

        PVPHelper.INSTANCE.getLogger().log(Level.INFO, String.format("loaded %s arenas", arenas.size()));
    }

    public void createArena(Arena arena) {
        try {
            File file = new File(arenaDataFolder, String.format("%s.nbt", arena.getName()));
            if (!file.exists()) file.createNewFile();
            NBTTagCompound compound = toCompound(arena);
            NBTUtils.writeTagToFile(compound, file);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        arenas.add(arena);
    }

    public void deleteArena(Arena arena) {
        File file = new File(arenaDataFolder, String.format("%s.nbt", arena.getName()));
        if (!file.exists()) return;
        file.delete();
        arenas.remove(arena);
    }

    @NotNull
    private NBTTagCompound toCompound(Arena arena) {
        Location pointA = arena.getPointA();
        Location pointB = arena.getPointB();
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("name", arena.getName());
        compound.setString("world", arena.getWorld().getName());
        compound.setDouble("x1", pointA.getX());
        compound.setDouble("y1", pointA.getY());
        compound.setDouble("z1", pointA.getZ());
        compound.setDouble("x2", pointB.getX());
        compound.setDouble("y2", pointB.getY());
        compound.setDouble("z2", pointB.getZ());
        return compound;
    }

    @NotNull
    private Arena fromCompound(NBTTagCompound compound) {
        World world = Bukkit.getWorld(compound.getString("world"));
        String name = compound.getString("name");
        Location pointA = new Location(world, compound.getDouble("x1"), compound.getDouble("y1"), compound.getDouble("z1"));
        Location pointB = new Location(world, compound.getDouble("x2"), compound.getDouble("y2"), compound.getDouble("z2"));
        return new Arena(name, world, pointA, pointB);
    }

    public Arena getArena(String name) {
        return arenas.stream().filter(arena -> arena.getName().equals(name)).findAny().orElse(null);
    }

    public boolean isPlayerInArena(Player player) {
        for (Arena arena : arenas) {
            if (!arena.isPlayerWithinBounds(player)) continue;
            return true;
        }
        return false;
    }

    public boolean isLocationInArena(Location location) {
        for (Arena arena : arenas) {
            if (!arena.isLocationWithinBounds(location)) continue;
            return true;
        }
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        ItemStack nmsItem = CraftItemStack.asNMSCopy(event.getItem());
        Player player = event.getPlayer();
        if (nmsItem.getTag() != null && nmsItem.getTag().hasKey("arenaCreator")) {
            event.setCancelled(true);
            switch (event.getAction()) {
                case LEFT_CLICK_BLOCK: // set pos1
                    creationPos1 = event.getClickedBlock().getLocation();
                    Utils.sendMessage(player, String.format("&bSet position 1 at %s, %s, %s", creationPos1.getX(), creationPos1.getY(), creationPos1.getZ()));
                    break;

                case RIGHT_CLICK_BLOCK: // set pos2
                    creationPos2 = event.getClickedBlock().getLocation();
                    Utils.sendMessage(player, String.format("&bSet position 2 at %s, %s, %s", creationPos2.getX(), creationPos2.getY(), creationPos2.getZ()));
                    break;
            }
        }
    }
}
