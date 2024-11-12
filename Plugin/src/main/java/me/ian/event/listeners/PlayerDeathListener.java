package me.ian.event.listeners;

import com.moandjiezana.toml.Toml;
import me.ian.Config;
import me.ian.PVPHelper;
import me.ian.mixin.event.PlayerPreDeathEvent;
import me.ian.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityDamageSource;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author SevJ6
 * Custom Death Messages
 */
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPreDeath(PlayerPreDeathEvent event) {
        Player player = event.getPlayer().getBukkitEntity();
        if (event.getSource().translationIndex.equals("explosion")) {
            if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent playerDamageEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();
                if (playerDamageEvent.getDamager() instanceof EnderCrystal) {
                    EnderCrystal crystal = (EnderCrystal) playerDamageEvent.getDamager();
                    if (crystal.getLastDamageCause() instanceof EntityDamageByEntityEvent) { // check crystal hitter
                        EntityDamageByEntityEvent crystalDamageEvent = (EntityDamageByEntityEvent) crystal.getLastDamageCause();
                        if (crystalDamageEvent.getDamager() instanceof Player) {
                            Player crystalHitter = (Player) crystalDamageEvent.getDamager();
                            Toml config = PVPHelper.INSTANCE.getRunningConfig().getToml();
                            if (Objects.equals(crystalHitter, player)) {
                                Utils.broadcastMessage(config.getString("crystal_player_suicide").replace("%victim%", player.getName()));
                            } else {
                                Utils.broadcastMessage(config.getString("crystal_player_kill").replace("%killer%", crystalHitter.getName()).replace("%victim%", player.getName()));
                            }
                        }
                    }
                }
            }
        } else {
            Bukkit.broadcast(toComponent(event.getSource(), event.getPlayer().getBukkitEntity()));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    private BaseComponent toComponent(DamageSource source, Player eliminated) {
        if (source instanceof EntityDamageSource) {
            if (source.getEntity() != null && source.getEntity().getBukkitEntity() instanceof Player) {
                Player killer = (Player) source.getEntity().getBukkitEntity();
                BaseComponent mainComponent = new TextComponent(Utils.translateChars(String.format("&3%s &4killed &3%s", killer.getName(), eliminated.getName())));
                if (killer.getInventory().getItemInMainHand() != null) {
                    ItemStack weapon = killer.getInventory().getItemInMainHand();
                    mainComponent.addExtra(Utils.translateChars(" &4using"));
                    BaseComponent weaponComponent = new TextComponent(Utils.translateChars("&6 " + (weapon.hasItemMeta() ? weapon.getItemMeta().getDisplayName() : weapon.getI18NDisplayName())));
                    weaponComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(convertItemStackToJson(weapon))}));
                    mainComponent.addExtra(weaponComponent);
                }

                return mainComponent;
            }
        }
        return new TextComponent(source.getLocalizedDeathMessage(Utils.getHandle(eliminated)).getText().replace(eliminated.getName(), String.format(Utils.translateChars("&r&3%s&r&4"), eliminated.getName())));
    }

    private String convertItemStackToJson(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = new NBTTagCompound();
        return nmsStack.save(compound).toString();
    }
}
