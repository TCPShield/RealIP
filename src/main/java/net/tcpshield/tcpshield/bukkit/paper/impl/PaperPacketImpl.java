package net.tcpshield.tcpshield.bukkit.paper.impl;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import net.tcpshield.tcpshield.abstraction.IPacket;

public class PaperPacketImpl implements IPacket {

    private final PlayerHandshakeEvent handshakeEvent;

    public PaperPacketImpl(PlayerHandshakeEvent handshakeEvent) {
        this.handshakeEvent = handshakeEvent;
    }

    @Override
    public String getRawPayload() {
        return handshakeEvent.getOriginalHandshake();
    }

    @Override
    public void modifyOriginalPacket(String hostname) {
        System.out.println("set hostname to " + hostname);
        handshakeEvent.setCancelled(false);
        handshakeEvent.setServerHostname(hostname);
    }
}
