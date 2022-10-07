package net.tcpshield.tcpshield.fabric.impl;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.text.LiteralText;
import net.tcpshield.tcpshield.fabric.mixin.ClientConnectionAccessor;
import net.tcpshield.tcpshield.provider.PlayerProvider;

import java.net.InetSocketAddress;

public class FabricPlayer implements PlayerProvider {

    private final ClientConnection connection;
    private String ip;

    public FabricPlayer(HandshakeC2SPacket packet, ClientConnection connection) {
        this.connection = connection;
        this.ip = ((InetSocketAddress) ((ClientConnectionAccessor) connection).getChannel().remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public String getUUID() {
        return "unknown";
    }

    @Override
    public String getName() {
        return "unknown";
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public void setIP(InetSocketAddress ip) {
        // At this point, the IP/connection believe the player has the IP of TCPShield.
        // The ip passed into this method contains their CORRECT data, which we have to assign to the player network connection.
        ((ClientConnectionAccessor) connection).setAddress(ip);
        this.ip = ((InetSocketAddress) ((ClientConnectionAccessor) connection).getChannel().remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public void disconnect() {
        connection.disconnect(new LiteralText("Connection failed. Please try again or contact an administrator."));
    }
}
