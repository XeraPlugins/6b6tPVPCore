package me.ian.general.listeners;

import com.moandjiezana.toml.Toml;
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
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

/**
 * @author SevJ6
 * Custom Death Messages
 */
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(event.getHand() == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand());
            if (nmsItem.getTag() != null && nmsItem.getTag().hasKey("EntityTag")) {
                if (nmsItem.getTag().getCompound("EntityTag").getString("id").equals("minecraft:creeper")) {
                    Utils.run(() -> {
                        event.getClickedBlock().getRelative(event.getBlockFace()).getLocation()
                                .getNearbyEntities(1, 1, 1)
                                .stream()
                                .filter(entity -> entity.getType() == EntityType.CREEPER)
                                .filter(entity -> entity.getTicksLived() < 3)
                                .forEach(entity -> {
                                    entity.setMetadata("placer", new FixedMetadataValue(PVPHelper.INSTANCE, player.getName()));
                                    ((Creeper) entity).setMaxFuseTicks(0);
                                });
                    });
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Toml config = PVPHelper.INSTANCE.getRunningConfig().getToml();
            EntityDamageByEntityEvent playerDamageEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();

            if (playerDamageEvent.getDamager() instanceof EnderCrystal) {
                EnderCrystal crystal = (EnderCrystal) playerDamageEvent.getDamager();
                if (crystal.getLastDamageCause() instanceof EntityDamageByEntityEvent) { // check crystal hitter
                    EntityDamageByEntityEvent crystalDamageEvent = (EntityDamageByEntityEvent) crystal.getLastDamageCause();
                    if (crystalDamageEvent.getDamager() instanceof Player) {
                        Player crystalHitter = (Player) crystalDamageEvent.getDamager();
                        if (Objects.equals(crystalHitter, player)) {
                            Utils.broadcastMessage(config.getString("crystal_player_suicide").replace("%victim%", player.getName()));
                        } else {
                            Utils.broadcastMessage(config.getString("crystal_player_kill").replace("%killer%", crystalHitter.getName()).replace("%victim%", player.getName()));
                        }
                    }
                }
                return;
            }

            if (playerDamageEvent.getDamager() instanceof Creeper) {
                Creeper creeper = (Creeper) playerDamageEvent.getDamager();
                Player placer = Bukkit.getPlayer(creeper.getMetadata("placer").get(0).asString());
                if (placer == null) return;
                if (Objects.equals(placer, player)) {
                    Utils.broadcastMessage(config.getString("creeper_player_suicide").replace("%victim%", player.getName()));
                } else {
                    Utils.broadcastMessage(config.getString("creeper_player_kill").replace("%killer%", placer.getName()).replace("%victim%", player.getName()));
                }
                return;
            }

            if (playerDamageEvent.getDamager() instanceof Player && player.getKiller() != null) {
                Player killer = player.getKiller();
                BaseComponent mainComponent = new TextComponent(Utils.translateChars(String.format("&3%s &4killed &3%s", killer.getName(), player.getName())));
                if (killer.getInventory().getItemInMainHand() != null) {
                    ItemStack weapon = killer.getInventory().getItemInMainHand();
                    mainComponent.addExtra(Utils.translateChars(" &4using"));
                    BaseComponent weaponComponent = new TextComponent(Utils.translateChars("&6 " + weapon.getI18NDisplayName()));
                    weaponComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(convertItemStackToJson(weapon))}));
                    mainComponent.addExtra(weaponComponent);
                }

                Bukkit.broadcast(mainComponent);
            }
        }
    }

    private String convertItemStackToJson(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = new NBTTagCompound();
        return nmsStack.save(compound).toString();
    }
}
