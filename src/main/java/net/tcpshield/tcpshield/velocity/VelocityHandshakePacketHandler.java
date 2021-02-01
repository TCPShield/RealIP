package net.tcpshield.tcpshield.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.velocity.impl.VelocityPacketImpl;
import net.tcpshield.tcpshield.velocity.impl.VelocityPlayerImpl;

public class VelocityHandshakePacketHandler {

    private final HandshakePacketHandler handshakePacketHandler;

    public VelocityHandshakePacketHandler(HandshakePacketHandler handshakePacketHandler) {
        this.handshakePacketHandler = handshakePacketHandler;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onHandshake(ConnectionHandshakeEvent e) {
        InboundConnection connection = e.getConnection();
        handleEvent(connection);
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onProxyPing(ProxyPingEvent e) {
        InboundConnection connection = e.getConnection();
        handleEvent(connection);
    }

    private void handleEvent(InboundConnection connection) {
        VelocityPlayerImpl player = new VelocityPlayerImpl(connection);
        if (player.isLegacy()) {
            player.disconnect();
            return;
        }

        IPacket packet = new VelocityPacketImpl(connection);

        handshakePacketHandler.onHandshake(packet, player);
    }
}
