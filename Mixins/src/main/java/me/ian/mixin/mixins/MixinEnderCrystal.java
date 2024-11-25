package me.ian.mixin.mixins;

import me.ian.mixin.event.EndCrystalCreateEvent;
import me.txmc.rtmixin.CallbackInfo;
import me.txmc.rtmixin.mixin.At;
import me.txmc.rtmixin.mixin.Inject;
import me.txmc.rtmixin.mixin.MethodInfo;
import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;

public class MixinEnderCrystal {

    @Inject(info = @MethodInfo(_class = EntityEnderCrystal.class, name = "<init>", sig = {World.class, double.class, double.class, double.class}, rtype = void.class), at = @At(pos = At.Position.TAIL))
    public static void mixinConstructorEnderCrystal(CallbackInfo ci) {
        EntityEnderCrystal crystal = (EntityEnderCrystal) ci.getSelf();
        Location location = new Location(crystal.world.getWorld(), crystal.locX, crystal.locY, crystal.locZ);
        Bukkit.getServer().getPluginManager().callEvent(new EndCrystalCreateEvent((EnderCrystal) crystal.getBukkitEntity(), location.getWorld(), location));
    }
}
