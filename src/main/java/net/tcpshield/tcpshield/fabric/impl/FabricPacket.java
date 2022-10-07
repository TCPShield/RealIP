package net.tcpshield.tcpshield.fabric.impl;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.tcpshield.tcpshield.fabric.mixin.HandshakeC2SPacketAccessor;
import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.util.exception.manipulate.PacketManipulationException;

public class FabricPacket implements PacketProvider  {

    private final HandshakeC2SPacket handshake;

    public FabricPacket(HandshakeC2SPacket handshake) {
        this.handshake = handshake;
    }

    @Override
    public String getPayloadString() {
        return ((HandshakeC2SPacketAccessor) handshake).getAddress();
    }

    @Override
    public void setPacketHostname(String hostname) throws PacketManipulationException {
        // NO OPERATION
    }
}
