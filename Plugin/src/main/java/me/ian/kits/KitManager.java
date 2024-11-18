package me.ian.kits;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.kits.gui.listener.InventoryClickListener;
import me.ian.utils.NBTUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.*;

@Getter
public class KitManager implements Listener {

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
                .filter(file -> !file.isDirectory() && file.getName().endsWith(".nbt"))
                .map(file -> loadKit(null, file.getName().replace(".nbt", "")))
                .peek(globalKits::add)
                .count();

        PVPHelper.INSTANCE.getLogger().info(String.format("Loaded %s global kits", count));
        PVPHelper.INSTANCE.registerListener(this);
        PVPHelper.INSTANCE.registerListener(new InventoryClickListener());
    }

    // Load User kits
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        File kitOwnerDataFolder = new File(userKitDataFolder, playerUUID.toString());

        // Ensure the directory exists
        if (!kitOwnerDataFolder.exists() || !kitOwnerDataFolder.isDirectory()) {
            kitOwnerDataFolder.mkdirs(); // Create the directory if it doesn't exist
            return; // No kits to load if the folder didn't exist
        }

        List<Kit> kits = userKits.computeIfAbsent(playerUUID, k -> new ArrayList<>());
        for (File file : Objects.requireNonNull(kitOwnerDataFolder.listFiles())) {
            if (!file.isDirectory() && file.getName().endsWith(".nbt")) {
                String kitName = file.getName().replace(".nbt", "");
                Kit kit = loadKit(playerUUID, kitName);
                if (kit != null) {
                    kits.add(kit); // Add to the user's kit list
                }
            }
        }

        PVPHelper.INSTANCE.getLogger().info(String.format("Loaded %d kits for player %s", kits.size(), player.getName()));
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

    /**
     * Retrieves a kit by name, either from the global kits or a specific user's kits.
     *
     * @param uuid The UUID of the kit owner. If null, the method searches the global kits.
     * @param name The name of the kit to retrieve. Case-insensitive.
     * @return The matching {@code Kit} object, or {@code null} if no matching kit is found.
     */
    public Kit getKit(UUID uuid, String name) {
        // Determine the source of kits: global or user-specific
        List<Kit> kits = (uuid == null) ? globalKits : userKits.getOrDefault(uuid, Collections.emptyList());

        // Find and return the matching kit
        return kits.stream()
                .filter(kit -> kit.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }
}
