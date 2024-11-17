package me.ian.kits;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.utils.NBTUtils;

import java.io.File;
import java.util.*;

@Getter
public class KitManager {

    private final List<Kit> globalKits;
    private final Map<UUID, List<Kit>> userKits;
    private final File userKitDataFolder;
    private final File globalKitDataFolder;

    public KitManager() {
        globalKits = new ArrayList<>();
        userKits = new HashMap<>();

        File kitDataFolder = new File(PVPHelper.INSTANCE.getDataFolder(), "kits");
        userKitDataFolder = new File(kitDataFolder, "user");
        globalKitDataFolder = new File(kitDataFolder, "global");
        try {
            if (!kitDataFolder.exists()) kitDataFolder.mkdirs();
            if (!userKitDataFolder.exists()) userKitDataFolder.mkdirs();
            if (!globalKitDataFolder.exists()) globalKitDataFolder.mkdirs();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Load Global Kits
        int count = (int) Arrays.stream(Objects.requireNonNull(globalKitDataFolder.listFiles()))
                .filter(file -> !file.isDirectory())
                .map(file -> loadKit(null, file.getName().replace(".nbt", "")))
                .peek(globalKits::add)
                .count();

        PVPHelper.INSTANCE.getLogger().info(String.format("Loaded %s global kits", count));
    }

    /**
     * Saves a kit to the file system.
     * The kit will be saved as a `.nbt` file in either the global data folder
     * or the owner's data folder, depending on whether it is a global kit.
     *
     * @param kit The kit to save.
     */
    public void saveKit(Kit kit) {
        final String FILE_NAME = String.format("%s.nbt", kit.getName());
        File kitFile;

        // Check if kit is global
        if (kit.isGlobal()) {
            kitFile = new File(globalKitDataFolder, FILE_NAME);
        } else {
            File kitOwnerDataFolder = new File(userKitDataFolder, kit.getOwner().toString());
            if (!kitOwnerDataFolder.exists()) kitOwnerDataFolder.mkdirs();
            kitFile = new File(kitOwnerDataFolder, FILE_NAME);
        }

        // Save the kit contents to the file.
        try {
            if (!kitFile.exists()) kitFile.createNewFile();
            NBTUtils.writeTagToFile(kit.getCompound(), kitFile);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Loads a kit from the file system.
     * Looks for the kit in the global data folder if `uuid` is null,
     * otherwise, looks in the specified user's data folder.
     *
     * @param uuid The UUID of the kit owner. Use null for global kits.
     * @param name The name of the kit to load.
     * @return The loaded kit, or null if the kit does not exist.
     */
    public Kit loadKit(UUID uuid, String name) {
        final String FILE_NAME = String.format("%s.nbt", name);
        File kitFile;

        // Check if kit is global
        if (uuid == null) {
            kitFile = new File(globalKitDataFolder, FILE_NAME);
        } else {
            File kitOwnerDataFolder = new File(userKitDataFolder, uuid.toString());
            kitFile = new File(kitOwnerDataFolder, FILE_NAME);
        }

        // Load kit contents from the file.
        if (!kitFile.exists()) return null;
        return new Kit(name, NBTUtils.readTagFromFile(kitFile), uuid);
    }

    /**
     * Removes a kit from the file system and any relevant in-memory collections.
     * Deletes the `.nbt` file and updates the respective list of kits.
     *
     * @param kit The kit to remove.
     */
    public void removeKit(Kit kit) {
        final String FILE_NAME = String.format("%s.nbt", kit.getName());
        File kitFile;

        // Check if kit is global
        if (kit.isGlobal()) {
            kitFile = new File(globalKitDataFolder, FILE_NAME);
            globalKits.remove(kit);
        } else {
            File kitOwnerDataFolder = new File(userKitDataFolder, kit.getOwner().toString());
            kitFile = new File(kitOwnerDataFolder, FILE_NAME);
            userKits.get(kit.getOwner()).remove(kit);
        }

        // Delete the kit file
        if (kitFile.exists()) kitFile.delete();
    }
}
