package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.arena.Arena;
import me.ian.arena.ArenaManager;
import me.ian.command.PluginCommand;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.rmi.CORBA.Util;
import java.util.Objects;

public class ArenaCommand extends PluginCommand implements CommandExecutor {

    public ArenaCommand() {
        super("arena", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            ArenaManager arenaManager = PVPHelper.INSTANCE.getArenaManager();

            switch (label) {
                case "arena":
                    Utils.sendMessage(player, String.format("&c%s", command.getUsage()));
                    break;
                case "createarena":
                    Location pos1 = arenaManager.getCreationPos1();
                    Location pos2 = arenaManager.getCreationPos2();
                    if (pos1 == null || pos2 == null) {
                        Utils.sendMessage(player, "&cMake sure you make a selection first");
                        return true;
                    }

                    if (!Objects.equals(pos1.getWorld(), pos2.getWorld())) {
                        Utils.sendMessage(player, "&cLocations must be in the same world");
                        return true;
                    }

                    if (args.length > 0) {
                        Arena arena = new Arena(args[0], player.getWorld(), pos1, pos2, args.length > 1 && args[1].equals("duel"));
                        PVPHelper.INSTANCE.getArenaManager().createArena(arena);
                        Utils.sendMessage(player, String.format("&bCreated arena &a%s", arena.getName()));
                    } else Utils.sendMessage(player, "&cEnter a name please.");
                    break;

                case "cleararenas":
                    arenaManager.getArenas().forEach(Arena::clear);
                    Utils.broadcastMessage(String.format("&b%s cleared all arenas.", player.getName()));
                    break;

                case "removearena":
                    if (args.length > 0) {
                        Arena arena = arenaManager.getArena(args[0]);
                        if (arena != null) {
                            arenaManager.deleteArena(arena);
                            Utils.sendMessage(player, String.format("&bDeleted arena &a%s", args[0]));
                        } else Utils.sendMessage(player, String.format("&cArena %s does not exist", args[0]));
                    } else Utils.sendMessage(player, "&cEnter a name please.");
                    break;

                case "listarenas":
                    player.sendMessage(getArenaList(arenaManager).toString());
                    break;
                case "arenawand":
                    ItemStack stick = new ItemStack(Items.STICK);
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setBoolean("arenaCreator", true);
                    NBTTagCompound display = new NBTTagCompound();
                    display.setString("Name", Utils.translateChars("&e&lArena Wand"));
                    compound.set("display", display);
                    stick.setTag(compound);
                    player.getInventory().addItem(CraftItemStack.asBukkitCopy(stick));
                    break;
            }
        }
        return true;
    }

    @NotNull
    private static StringBuilder getArenaList(ArenaManager arenaManager) {
        StringBuilder arenaList = new StringBuilder("Arenas and their bounding points:\n");
        for (Arena arena : arenaManager.getArenas()) {
            Location pointA = arena.getPointA();
            Location pointB = arena.getPointB();

            arenaList.append(String.format(
                    "Arena: %s\n  - Point A: (%.2f, %.2f, %.2f) in %s\n  - Point B: (%.2f, %.2f, %.2f) in %s\n",
                    arena.getName(),
                    pointA.getX(), pointA.getY(), pointA.getZ(), pointA.getWorld().getName(),
                    pointB.getX(), pointB.getY(), pointB.getZ(), pointB.getWorld().getName()
            ));
        }
        return arenaList;
    }
}
