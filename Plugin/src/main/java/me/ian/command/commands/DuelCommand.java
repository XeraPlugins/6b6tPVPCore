package me.ian.command.commands;

import me.ian.PVPHelper;
import me.ian.arena.Arena;
import me.ian.arena.ArenaManager;
import me.ian.command.PluginCommand;
import me.ian.duels.Duel;
import me.ian.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelCommand extends PluginCommand implements CommandExecutor {
    private final Map<UUID, UUID> duelRequests = new HashMap<>();

    public DuelCommand() {
        super("duel", false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            Utils.sendMessage(player, "&cUsage: /duel <player>, /duel accept, or /duel decline");
            return true;
        }

        ArenaManager arenaManager = PVPHelper.INSTANCE.getArenaManager();
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "accept": {
                if (!duelRequests.containsKey(player.getUniqueId())) {
                    Utils.sendMessage(player, "&cYou have no duel requests.");
                    return true;
                }

                UUID challengerUUID = duelRequests.get(player.getUniqueId());
                Player challenger = Bukkit.getPlayer(challengerUUID);
                if (challenger == null || !challenger.isOnline()) {
                    Utils.sendMessage(player, "&cThe player who challenged you is no longer online.");
                    duelRequests.remove(player.getUniqueId());
                    return true;
                }

                if (arenaManager.isPlayerInArena(player)) {
                    Utils.sendMessage(player, "&cYou can not accept a duel request while you are in an arena.");
                    duelRequests.remove(player.getUniqueId());
                    return true;
                }

                Arena arena = PVPHelper.INSTANCE.getDuelManager().findEmptyArena();
                if (arena == null) {
                    Utils.sendMessage(player, "&cAll duel arenas are currently occupied. Please try again later.");
                    duelRequests.remove(player.getUniqueId());
                    return true;
                }

                Duel duel = new Duel(arena, Arrays.asList(challenger, player));
                PVPHelper.INSTANCE.getDuelManager().getDuels().add(duel);
                duel.start();

                Utils.sendMessage(player, "&bYou accepted the duel request from &a" + challenger.getName() + "&b!");
                Utils.sendMessage(challenger, "&bYour duel request was accepted by &a" + player.getName() + "&b!");
                duelRequests.remove(player.getUniqueId());
                return true;
            }
            case "decline": {
                if (!duelRequests.containsKey(player.getUniqueId())) {
                    Utils.sendMessage(player, "&cYou have no duel requests.");
                    return true;
                }

                UUID challengerUUID = duelRequests.get(player.getUniqueId());
                Player challenger = Bukkit.getPlayer(challengerUUID);
                if (challenger != null && challenger.isOnline()) {
                    Utils.sendMessage(challenger, "&cYour duel request to &a" + player.getName() + " &cwas declined.");
                }

                Utils.sendMessage(player, "&bYou declined the duel request.");
                duelRequests.remove(player.getUniqueId());
                return true;
            }
            default: {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    Utils.sendMessage(player, "&cThe player &a" + args[0] + " &cis not online.");
                    return true;
                }

                if (player.equals(target)) {
                    Utils.sendMessage(player, "&cYou cannot duel yourself.");
                    return true;
                }

                if (arenaManager.isPlayerInArena(target)) {
                    Utils.sendMessage(player, "&c" + target.getName() + " is currently inside an arena. You can not request to duel them at this time.");
                    return true;
                }

                if (duelRequests.containsValue(player.getUniqueId())) {
                    Utils.sendMessage(player, "&cYou already have an outgoing duel request. Wait for it to be accepted or declined.");
                    return true;
                }

                if (duelRequests.containsValue(target.getUniqueId())) {
                    Utils.sendMessage(player, "&cThe player &a" + target.getName() + " &calready has an outgoing duel request.");
                    return true;
                }

                if (duelRequests.containsKey(player.getUniqueId())) {
                    Utils.sendMessage(player, "&cYou already have an incoming duel request from &a" + Bukkit.getPlayer(duelRequests.get(player.getUniqueId())).getName());
                    return true;
                }

                if (duelRequests.containsKey(target.getUniqueId())) {
                    Utils.sendMessage(player, "&cThe player &a" + target.getName() + " &calready has an incoming duel request.");
                    return true;
                }

                duelRequests.put(target.getUniqueId(), player.getUniqueId());

                Utils.sendMessage(player, "&bYou sent a duel request to &a" + target.getName() + "&b!");

                TextComponent request = new TextComponent(Utils.translateChars(String.format("&b%s has challenged you to a duel ", player.getName())));
                TextComponent accept = new TextComponent(Utils.translateChars("&a&l[ACCEPT]"));
                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Utils.translateChars("&aClick to ACCEPT duel"))}));
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept"));
                TextComponent deny = new TextComponent(Utils.translateChars("&4&l[DECLINE]"));
                deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Utils.translateChars("&4Click to DECLINE duel"))}));
                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline"));

                target.sendMessage(request, new TextComponent("\n"), accept, new TextComponent(" "), deny);

                Bukkit.getScheduler().runTaskLater(PVPHelper.INSTANCE, () -> {
                    if (duelRequests.containsKey(target.getUniqueId()) && duelRequests.get(target.getUniqueId()).equals(player.getUniqueId())) {
                        Utils.sendMessage(player, "&cYour duel request to &a" + target.getName() + " &chas timed out.");
                        Utils.sendMessage(target, "&cYour duel request from &a" + player.getName() + " &chas timed out.");
                        duelRequests.remove(target.getUniqueId());
                    }
                }, 20 * 60L); // 60-second timeout.
                return true;
            }
        }
    }
}
