package me.ian.general;

import me.ian.PVPHelper;
import me.ian.general.listeners.*;
import me.ian.general.listeners.patches.*;
import me.ian.lobby.LobbyProtection;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayInUseItem;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SevJ6
 */
public class EventManager {

    private final List<Listener> listeners;

    public EventManager() {
        listeners = new ArrayList<>();

        // add listeners
        listeners.add(new PlayerDeathListener());
        listeners.add(new Boatfly());
        listeners.add(new EntityCollisions());
        listeners.add(new ProjectileCrash());
        listeners.add(new ProjectileVelocity());
        listeners.add(new DispenserCrash());
        listeners.add(new ItemRevertListener());
        listeners.add(new LobbyProtection());
        listeners.add(new BedPlacementListener());
        listeners.add(new CommandListener());
        PVPHelper.INSTANCE.getDispatcher().register(new PacketLimit(), (Class<? extends Packet<?>>) null);
        PVPHelper.INSTANCE.getDispatcher().register(new FastCrystalListener(), PacketPlayInUseItem.class);
    }

    public void registerEvents() {
        listeners.forEach(listener -> PVPHelper.INSTANCE.registerListener(listener));
    }
}
