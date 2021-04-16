package net.tcpshield.tcpshield.bukkit.paper.impl;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import net.kyori.adventure.text.Component;
import net.tcpshield.tcpshield.abstraction.IPlayer;

import java.net.InetSocketAddress;

public class PaperPlayerImpl implements IPlayer {

    private final PlayerHandshakeEvent handshakeEvent;

    public PaperPlayerImpl(PlayerHandshakeEvent handshakeEvent) {
        this.handshakeEvent = handshakeEvent;
    }

    @Override
    public String getIP() {
        return handshakeEvent.getOriginalSocketAddressHostname();
    }

    @Override
    public void setIP(InetSocketAddress ip) {
        handshakeEvent.setSocketAddressHostname(ip.getAddress().getHostAddress());
    }

    @Override
    public void disconnect() {
        handshakeEvent.setCancelled(false);
        handshakeEvent.failMessage(Component.text("Connection failed. Please try again or contact an administrator."));
        handshakeEvent.setFailed(true);
    }
}
