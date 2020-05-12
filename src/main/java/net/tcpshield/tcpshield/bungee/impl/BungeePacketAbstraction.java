package net.tcpshield.tcpshield.bungee.impl;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.protocol.packet.Handshake;
import net.tcpshield.tcpshield.ReflectionUtils;
import net.tcpshield.tcpshield.abstraction.PacketAbstraction;

import java.net.InetSocketAddress;

public class BungeePacketAbstraction implements PacketAbstraction {

    private final Handshake handshake;
    private final PendingConnection pendingConnection;

    public BungeePacketAbstraction(Handshake handshake, PendingConnection pendingConnection) {
        this.handshake = handshake;
        this.pendingConnection = pendingConnection;
    }

    @Override
    public String getRawPayload() {
        return handshake.getHost();
    }

    @Override
    public void modifyOriginalPacket(String hostname) throws Exception {
        InetSocketAddress virtualHost = new InetSocketAddress(hostname, handshake.getPort());
        try {
            ReflectionUtils.setFinalField(pendingConnection, "virtualHost", virtualHost);
        } catch (Exception ex) {
            ReflectionUtils.setFinalField(pendingConnection, "vHost", virtualHost);
        }

        ReflectionUtils.setField(handshake, "host", hostname);
    }
}
