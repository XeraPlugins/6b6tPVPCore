package me.ian.portal;

import lombok.Getter;
import me.ian.PVPHelper;
import me.ian.mixin.event.ServerTickEvent;
import me.ian.utils.IManagerData;
import me.ian.utils.NBTUtils;
import me.ian.utils.area.BoundingBox;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

@Getter
public class PortalManager implements Listener, IManagerData<Portal> {

    private final File portalDataFolder;
    private final List<Portal> portals;

    public PortalManager() {
        this.portals = new ArrayList<>();
        portalDataFolder = new File(PVPHelper.INSTANCE.getDataFolder(), "portals");
        if (!portalDataFolder.exists()) portalDataFolder.mkdirs();

        // Load all portals
        Arrays.stream(Objects.requireNonNull(portalDataFolder.listFiles())).filter(file -> file.getName().endsWith(".nbt")).forEach(file -> {
            NBTTagCompound compound = NBTUtils.readTagFromFile(file);
            Portal portal = fromCompound(compound);
            portals.add(portal);
        });

        PVPHelper.INSTANCE.registerListener(this);
        PVPHelper.INSTANCE.getLogger().log(Level.INFO, String.format("loaded %s portals", portals.size()));
    }

    @EventHandler
    public void onMove(ServerTickEvent event) {
        Bukkit.getOnlinePlayers().forEach(player ->
                portals.stream()
                        .filter(portal -> portal.getBoundingBox().isPlayerWithinBounds(player))
                        .forEach(portal -> portal.teleport(player))
        );
    }

    @Override
    public void create(Portal portal) {
        try {
            File file = new File(portalDataFolder, String.format("%s.nbt", portal.getName()));
            if (!file.exists()) file.createNewFile();
            NBTUtils.writeTagToFile(toCompound(portal), file);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        portals.add(portal);
    }

    @Override
    public void delete(Portal portal) {
        File file = new File(portalDataFolder, String.format("%s.nbt", portal.getName()));
        if (!file.exists()) return;
        file.delete();
        portals.remove(portal);
    }

    @Override
    public NBTTagCompound toCompound(Portal portal) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("name", portal.getName());
        portal.getBoundingBox().write(compound);
        NBTTagCompound exitLocationTag = new NBTTagCompound();
        Location exitLocation = portal.getExitLocation();
        exitLocationTag.setString("world", exitLocation.getWorld().getName());
        exitLocationTag.setDouble("x", exitLocation.getX());
        exitLocationTag.setDouble("y", exitLocation.getY());
        exitLocationTag.setDouble("z", exitLocation.getZ());
        exitLocationTag.setFloat("yaw", exitLocation.getYaw());
        exitLocationTag.setFloat("pitch", exitLocation.getPitch());
        compound.set("ExitLocation", exitLocationTag);
        return null;
    }

    @Override
    public Portal fromCompound(NBTTagCompound compound) {
        String name = compound.getString("name");
        BoundingBox boundingBox = BoundingBox.read(compound);

        // Exit Location
        NBTTagCompound exitLocationTag = compound.getCompound("ExitLocation");
        Location location = new Location(
                Bukkit.getWorld(exitLocationTag.getString("world")),
                exitLocationTag.getDouble("x"),
                exitLocationTag.getDouble("y"),
                exitLocationTag.getDouble("z"),
                exitLocationTag.getFloat("yaw"),
                exitLocationTag.getFloat("pitch")
        );

        return new Portal(name, boundingBox, location);
    }
}
