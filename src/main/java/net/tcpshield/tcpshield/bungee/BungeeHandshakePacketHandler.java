package net.tcpshield.tcpshield.bungee;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.PacketAbstraction;
import net.tcpshield.tcpshield.abstraction.PlayerAbstraction;
import net.tcpshield.tcpshield.bungee.impl.BungeeConfigAbstraction;
import net.tcpshield.tcpshield.bungee.impl.BungeePacketAbstraction;
import net.tcpshield.tcpshield.bungee.impl.BungeePlayerAbstraction;

public class BungeeHandshakePacketHandler implements Listener {

    private final HandshakePacketHandler handshakePacketHandler;

    public BungeeHandshakePacketHandler(Plugin plugin) {
        this.handshakePacketHandler = new HandshakePacketHandler(plugin.getLogger(), new BungeeConfigAbstraction(plugin));
    }

    @EventHandler(priority = -64)
    public void onProxyPingEvent(ProxyPingEvent e) {
        PendingConnection connection = e.getConnection();
        if (!connection.isLegacy()) return;

        connection.disconnect();
    }

    @EventHandler(priority = -64)
    public void onPlayerHandshake(PlayerHandshakeEvent e) {
        PacketAbstraction packetAbstraction = new BungeePacketAbstraction(e.getHandshake(), e.getConnection());
        PlayerAbstraction playerAbstraction = new BungeePlayerAbstraction(e.getConnection());

        handshakePacketHandler.onHandshake(packetAbstraction, playerAbstraction);
    }
}
