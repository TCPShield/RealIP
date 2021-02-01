package net.tcpshield.tcpshield.geyser.impl;

import net.tcpshield.tcpshield.abstraction.IPlayer;
import org.geysermc.floodgate.api.handshake.HandshakeData;

import java.net.InetSocketAddress;

public class GeyserPlayerImpl implements IPlayer {

    private final HandshakeData handshakeData;

    public GeyserPlayerImpl(HandshakeData handshakeData) {
        this.handshakeData = handshakeData;
    }

    @Override
    public String getUUID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getIP() {
        return ((InetSocketAddress) handshakeData.getChannel().remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public void setIP(InetSocketAddress ip) {
        System.out.println("\"setting ip to\" = " + ip);
        handshakeData.setBedrockIp(ip.getAddress().getHostAddress());
    }

    @Override
    public void disconnect() {
        handshakeData.getChannel().close();
    }
}
