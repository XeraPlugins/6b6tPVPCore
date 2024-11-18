package me.ian.kits;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ian.PVPHelper;
import me.ian.kits.event.PlayerEquipKitEvent;
import me.ian.utils.NBTUtils;
import me.ian.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Kit {

    private final String name;
    private final NBTTagCompound compound;
    private final UUID owner;

    public boolean isGlobal() {
        return owner == null;
    }

    public void equip(Player player) {
        PlayerEquipKitEvent event = new PlayerEquipKitEvent(player, this);
        PVPHelper.INSTANCE.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            NBTUtils.setPlayerInventoryFromTag(player, compound);
            if (player.hasMetadata("kit_gui")) player.removeMetadata("kit_gui", PVPHelper.INSTANCE);
            Utils.sendMessage(player, String.format("&bEquipped kit &a%s", name));
        }
    }
}
