package net.tcpshield.tcpshield.geyser.impl;

import net.tcpshield.tcpshield.abstraction.IPacket;
import org.geysermc.floodgate.api.handshake.HandshakeData;

public class GeyserPacketImpl implements IPacket {

    private final HandshakeData handshakeData;

    public GeyserPacketImpl(HandshakeData handshakeData) {
        this.handshakeData = handshakeData;
    }

    @Override
    public String getRawPayload() {
        return handshakeData.getHostname();
    }

    @Override
    public void modifyOriginalPacket(String hostname) {
        handshakeData.setHostname(hostname);
    }
}
