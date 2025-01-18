package me.ian.arena;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.utils.IManagerData;
import me.ian.utils.NBTUtils;
import me.ian.utils.area.BoundingBox;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

@Getter
public class ArenaManager implements Listener, IManagerData<Arena> {

    private final File arenaDataFolder;
    private final List<Arena> arenas;

    public ArenaManager() {
        this.arenas = new ArrayList<>();
        arenaDataFolder = new File(PVPHelper.INSTANCE.getDataFolder(), "arenas");
        if (!arenaDataFolder.exists()) arenaDataFolder.mkdirs();

        // Load all arenas
        Arrays.stream(Objects.requireNonNull(arenaDataFolder.listFiles())).filter(file -> file.getName().endsWith(".nbt")).forEach(file -> {
            NBTTagCompound compound = NBTUtils.readTagFromFile(file);
            Arena arena = fromCompound(compound);
            arenas.add(arena);
        });

        PVPHelper.INSTANCE.registerListener(this);
        PVPHelper.INSTANCE.getLogger().log(Level.INFO, String.format("loaded %s arenas", arenas.size()));
        ArenaCleaner.initializeScheduler();
    }

    @Override
    public void create(Arena arena) {
        try {
            File file = new File(arenaDataFolder, String.format("%s.nbt", arena.getName()));
            if (!file.exists()) file.createNewFile();
            NBTUtils.writeTagToFile(toCompound(arena), file);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        arenas.add(arena);
    }

    @Override
    public void delete(Arena arena) {
        File file = new File(arenaDataFolder, String.format("%s.nbt", arena.getName()));
        if (!file.exists()) return;
        file.delete();
        arenas.remove(arena);
    }

    @Override
    public NBTTagCompound toCompound(Arena arena) {
        NBTTagCompound compound = new NBTTagCompound();
        arena.getBoundingBox().write(compound);
        compound.setString("name", arena.getName());
        compound.setBoolean("isDuelArena", arena.isDuelArena());
        return compound;
    }

    @Override
    public Arena fromCompound(NBTTagCompound compound) {
        String name = compound.getString("name");
        BoundingBox boundingBox = BoundingBox.read(compound);
        boolean isDuelArena = compound.getBoolean("isDuelArena");
        return new Arena(name, boundingBox, isDuelArena);
    }

    public Arena getArena(String name) {
        return arenas.stream().filter(arena -> arena.getName().equals(name)).findAny().orElse(null);
    }

    private boolean isWithinBounds(Function<Arena, Boolean> condition) {
        return arenas.stream().anyMatch(condition::apply);
    }

    public boolean isPlayerInArena(Player player) {
        return isWithinBounds(arena -> arena.isPlayerWithinBounds(player));
    }

    public boolean isLocationInArena(Location location) {
        return isWithinBounds(arena -> arena.isLocationWithinBounds(location));
    }
}
