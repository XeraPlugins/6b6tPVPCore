package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.lobby.npc.custom.Cashier;
import me.ian.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author SevJ6
 */
public class SpawnNPCCommand extends PluginCommand implements CommandExecutor {
    public SpawnNPCCommand() {
        super("spawnnpc", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Cashier cashier = new Cashier(player.getLocation(), "CashierTest", PlayerUtils.getSkinProperties("8e176c5ac26d4c148efe77b598b8b3ea"), true);
        PVPHelper.INSTANCE.getNpcManager().addNPC(cashier);
        return true;
    }
}
