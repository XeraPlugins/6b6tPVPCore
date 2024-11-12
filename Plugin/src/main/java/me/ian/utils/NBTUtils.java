package me.ian.utils;

import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Method;

/**
 * @author SevJ6
 */
public class NBTUtils {

    private static Method loadM;
    private static Method createTagM;

    static {
        try {
            createTagM = NBTBase.class.getDeclaredMethod("createTag", byte.class);
            createTagM.setAccessible(true);
            loadM = NBTBase.class.getDeclaredMethod("load", DataInput.class, int.class, NBTReadLimiter.class);
            loadM.setAccessible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static NBTBase readBaseFromInput(DataInput input) throws Throwable {
        byte typeId = input.readByte();
        if (typeId == 0) return NBTTagEnd.class.newInstance();
        NBTBase base = (NBTBase) createTagM.invoke(NBTBase.class, typeId);
        input.readUTF();
        loadM.invoke(base, input, 0, NBTReadLimiter.a);
        return base;
    }

    @SneakyThrows
    public static NBTTagCompound readNBT(DataInput input) {
        return (NBTTagCompound) readBaseFromInput(input);
    }

    @SneakyThrows
    private static void writeNBT(NBTTagCompound compound, DataOutput output) {
        output.writeByte(compound.getTypeId());
        if (compound.getTypeId() == 0) return;
        output.writeUTF("");
        Method writeM = NBTBase.class.getDeclaredMethod("write", DataOutput.class);
        writeM.setAccessible(true);
        writeM.invoke(compound, output);
    }

    @SneakyThrows
    public static NBTTagCompound readTagFromFile(File file) {
        FileInputStream fis = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fis);
        NBTTagCompound compound = readNBT(in);
        in.close();
        fis.close();
        return compound;
    }

    @SneakyThrows
    public static void writeTagToFile(NBTTagCompound compound, File file) {
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream out = new DataOutputStream(fos);
        writeNBT(compound, out);
        out.flush();
        out.close();
        fos.close();
    }

    public static NBTTagCompound saveLocationToTag(Location location) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.set("world", new NBTTagString(location.getWorld().getName()));
        tag.set("x", new NBTTagDouble(location.getX()));
        tag.set("y", new NBTTagDouble(location.getY()));
        tag.set("z", new NBTTagDouble(location.getZ()));
        tag.set("yaw", new NBTTagFloat(location.getYaw()));
        tag.set("pitch", new NBTTagFloat(location.getPitch()));
        return tag;
    }

    public static void writeLocationToTag(NBTTagCompound compound, Location location) {
        compound.setString("world", location.getWorld().getName());
        compound.setDouble("x", location.getX());
        compound.setDouble("y", location.getY());
        compound.setDouble("z", location.getZ());
        compound.setFloat("yaw", location.getYaw());
        compound.setFloat("pitch", location.getPitch());
    }

    public static Location readLocationFromTag(NBTTagCompound compound) {
        if (!(compound.hasKeyOfType("world", 8) && compound.hasKeyOfType("x", 6) && compound.hasKeyOfType("y", 6) && compound.hasKeyOfType("z", 6) && compound.hasKeyOfType("yaw", 5) && compound.hasKeyOfType("pitch", 5))) {
            return null;
        }
        World world = Bukkit.getWorld(compound.getString("world"));
        if (world == null) {
            return null;
        }
        return new Location(world, compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"), compound.getFloat("yaw"), compound.getFloat("pitch"));
    }

    public static void setInventoryFromTag(Player player, NBTTagCompound compound) {
        if (player == null || !player.isOnline()) return;
        EntityPlayer nmsPlayer = Utils.getHandle(player);
        nmsPlayer.inventory.b(compound.getList("InvContents", 10));
    }
}
