package me.ian.general.listeners.patches;

import me.ian.PVPHelper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.logging.Level;

public class ProjectileVelocity implements Listener {

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Projectile projectile = event.getEntity();
        double velocity = projectile.getVelocity().lengthSquared();
        event.setCancelled(velocity > 10D);
        PVPHelper.INSTANCE.getLogger().log(Level.INFO, String.format("[Projectile Velocity] Stopped %s from shooting an arrow with velocity of %s", ((Player) event.getEntity().getShooter()).getName(), velocity));
    }
}
