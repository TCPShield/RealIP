package net.tcpshield.tcpshield.bukkit.paper.impl;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.tcpshield.tcpshield.abstraction.IPacket;

public class PaperServerListPacketImpl implements IPacket {

    private final PaperServerListPingEvent pingEvent;

    public PaperServerListPacketImpl(PaperServerListPingEvent pingEvent) {
        this.pingEvent = pingEvent;
    }

    @Override
    public String getRawPayload() {
        return null;
    }

    @Override
    public void modifyOriginalPacket(String hostname) {
        pingEvent.setCancelled(false);
    }
}
