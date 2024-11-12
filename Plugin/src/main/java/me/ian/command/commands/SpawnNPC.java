package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.command.PluginCommand;
import me.ian.lobby.npc.custom.Cashier;
import me.ian.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnNPC extends PluginCommand implements CommandExecutor {
    public SpawnNPC() {
        super("spawnnpc", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        String[] textures = PlayerUtils.getSkinProperties("8e176c5ac26d4c148efe77b598b8b3ea");
        Cashier cashier = new Cashier(player.getLocation(), "CashierNigga", textures[0], textures[1], true);
        PVPHelper.INSTANCE.getNpcManager().addNPC(cashier);
        return true;
    }
}
