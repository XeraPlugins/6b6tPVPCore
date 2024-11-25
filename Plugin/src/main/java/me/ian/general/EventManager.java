package me.ian.general;

import me.ian.PVPHelper;
import me.ian.general.listeners.*;
import me.ian.general.listeners.patches.*;
import me.ian.lobby.LobbyProtection;
import me.ian.lobby.frames.FrameListeners;
import me.txmc.protocolapi.PacketEventDispatcher;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
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
        PacketEventDispatcher dispatcher = PVPHelper.INSTANCE.getDispatcher();

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
        listeners.add(new BlockIgniteListener());
        listeners.add(new ItemConsumeListener());

        // Create instance of Frame Listeners to register as both a PacketListener and a BukkitListener
        FrameListeners frameListeners = new FrameListeners();
        listeners.add(frameListeners);
        dispatcher.register(frameListeners, PacketPlayInUseEntity.class);

        dispatcher.register(new PacketLimit(), (Class<? extends Packet<?>>) null);
    }

    public void registerEvents() {
        listeners.forEach(listener -> PVPHelper.INSTANCE.registerListener(listener));
    }
}
