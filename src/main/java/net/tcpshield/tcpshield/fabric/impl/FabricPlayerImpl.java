package net.tcpshield.tcpshield.fabric.impl;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.text.LiteralText;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.exception.IPModificationFailureException;
import net.tcpshield.tcpshield.fabric.mixin.ClientConnectionAccessor;

import java.net.InetSocketAddress;

public class FabricPlayerImpl implements IPlayer {

    private final ClientConnection connection;
    private String ip;

    public FabricPlayerImpl(HandshakeC2SPacket packet, ClientConnection connection) {
        this.connection = connection;
        this.ip = ((InetSocketAddress) ((ClientConnectionAccessor) connection).getChannel().remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public void setIP(InetSocketAddress ip) throws IPModificationFailureException {
        // At this point, the IP/connection believe the player has the IP of TCPShield.
        // The ip passed into this method contains their CORRECT data, which we have to assign to the player network connection.
        ((ClientConnectionAccessor) connection).setAddress(ip);
        this.ip = ((InetSocketAddress) ((ClientConnectionAccessor) connection).getChannel().remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public void disconnect() {
        connection.send(new LoginDisconnectS2CPacket(new LiteralText("Connection failed. Please try again or contact an administrator.")));
        connection.disconnect(new LiteralText("Connection failed. Please try again or contact an administrator."));
    }
}