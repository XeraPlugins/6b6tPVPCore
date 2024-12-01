package me.ian.mixin.mixins;


import me.ian.mixin.event.ItemCreateEvent;
import me.txmc.rtmixin.CallbackInfo;
import me.txmc.rtmixin.mixin.At;
import me.txmc.rtmixin.mixin.Inject;
import me.txmc.rtmixin.mixin.MethodInfo;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Bukkit;

public class MixinItemStack {

    @Inject(
            info = @MethodInfo(_class = ItemStack.class, name = "<init>", sig = {Item.class, int.class, int.class, boolean.class}, rtype = ItemStack.class),
            at = @At(pos = At.Position.TAIL)
    )
    public static void mixinItemStack(CallbackInfo ci) {
        ItemStack itemStack = (ItemStack) ci.getSelf();
        if (itemStack.getItem() == Item.getById(0)) return;
        ItemCreateEvent event = new ItemCreateEvent(itemStack);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            itemStack.setCount(-1);
        }
    }
}
