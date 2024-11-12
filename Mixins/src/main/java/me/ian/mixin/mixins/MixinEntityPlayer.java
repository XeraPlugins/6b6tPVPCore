package me.ian.mixin.mixins;

import me.ian.mixin.event.PlayerPreDeathEvent;
import me.txmc.rtmixin.CallbackInfo;
import me.txmc.rtmixin.mixin.At;
import me.txmc.rtmixin.mixin.Inject;
import me.txmc.rtmixin.mixin.MethodInfo;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;

public class MixinEntityPlayer {

    @Inject(info = @MethodInfo(_class = EntityPlayer.class, name = "die", sig = DamageSource.class, rtype = void.class), at = @At(pos = At.Position.HEAD))
    public static void onDie(CallbackInfo ci) {
        EntityPlayer player = (EntityPlayer) ci.getSelf();
        DamageSource source = (DamageSource) ci.getParameters()[0];
        PlayerPreDeathEvent event = new PlayerPreDeathEvent(player, source);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.setHealth(player.getMaxHealth());
            ci.cancel();
        }
    }
}
