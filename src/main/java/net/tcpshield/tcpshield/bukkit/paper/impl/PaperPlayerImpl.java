package net.tcpshield.tcpshield.bukkit.paper.impl;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import net.tcpshield.tcpshield.abstraction.IPlayer;

import java.net.InetSocketAddress;
import java.util.UUID;

public class PaperPlayerImpl implements IPlayer {

    private final PlayerHandshakeEvent handshakeEvent;

    public PaperPlayerImpl(PlayerHandshakeEvent handshakeEvent) {
        this.handshakeEvent = handshakeEvent;
    }

    @Override
    public String getUUID() {
        UUID uuid = handshakeEvent.getUniqueId();
        if (uuid == null) return "unknown";

        return uuid.toString();
    }

    @Override
    public String getName() {
        return "unknown";
    }

    @Override
    public String getIP() {
        return handshakeEvent.getSocketAddressHostname();
    }

    @Override
    public void setIP(InetSocketAddress ip) {
        handshakeEvent.setSocketAddressHostname(ip.getAddress().getHostAddress());
    }

    @Override
    public void disconnect() {
        handshakeEvent.setFailMessage("Connection failed. Please try again or contract an administrator.");
        handshakeEvent.setFailed(true);
    }
}
