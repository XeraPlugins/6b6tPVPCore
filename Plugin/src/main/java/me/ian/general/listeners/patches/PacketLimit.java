package me.ian.general.listeners.patches;

import me.ian.PVPHelper;
import me.ian.ViolationManager;
import me.ian.utils.PlayerUtils;
import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;

public class PacketLimit extends ViolationManager implements PacketListener {

    private final long RATE_LIMIT;

    public PacketLimit() {
        super(1, 400);
        RATE_LIMIT = PVPHelper.INSTANCE.getRunningConfig().getToml().getLong("packet_limit", 400L);
    }

    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        increment(event.getPlayer().getUniqueId().hashCode());
        int vls = getVLS(event.getPlayer().getUniqueId().hashCode());
        if (vls > RATE_LIMIT) {
            remove(event.getPlayer().getUniqueId().hashCode());
            PlayerUtils.kick(event.getPlayer(), String.format("Packet-per-second limit reached. %s vls", vls));
        }
    }

    @Override
    public void outgoing(PacketEvent.Outgoing event) throws Throwable {

    }
}
