package net.tcpshield.tcpshield.fabric.impl;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.fabric.mixin.HandshakeC2SPacketAccessor;

public class FabricPacketImpl implements IPacket {

    private final HandshakeC2SPacket handshake;

    public FabricPacketImpl(HandshakeC2SPacket handshake) {
        this.handshake = handshake;
    }

    @Override
    public String getRawPayload() {
        return ((HandshakeC2SPacketAccessor) handshake).getAddress();
    }

    @Override
    public void modifyOriginalPacket(String hostname) throws Exception {
        // NO OPERATION
    }
}