package me.ian.general.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class ItemConsumeListener implements Listener {

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getItem().getItemMeta() != null && event.getItem().getItemMeta() instanceof PotionMeta) {
            event.setReplacement(new ItemStack(Material.AIR));
        }
    }
}
