package net.tcpshield.tcpshield.bungee;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.bungee.impl.BungeeConfigImpl;
import net.tcpshield.tcpshield.bungee.impl.BungeePacketImpl;
import net.tcpshield.tcpshield.bungee.impl.BungeePlayerImpl;

public class BungeeHandshakePacketHandler implements Listener {

    private final HandshakePacketHandler handshakePacketHandler;

    public BungeeHandshakePacketHandler(Plugin plugin) {
        this.handshakePacketHandler = new HandshakePacketHandler(plugin.getLogger(), new BungeeConfigImpl(plugin));
    }

    @EventHandler(priority = -64)
    public void onProxyPingEvent(ProxyPingEvent e) {
        PendingConnection connection = e.getConnection();
        if (!connection.isLegacy()) return;

        connection.disconnect();
    }

    @EventHandler(priority = -64)
    public void onPlayerHandshake(PlayerHandshakeEvent e) {
        IPacket packet = new BungeePacketImpl(e.getHandshake(), e.getConnection());
        IPlayer player = new BungeePlayerImpl(e.getConnection());

        handshakePacketHandler.onHandshake(packet, player);
    }

}
