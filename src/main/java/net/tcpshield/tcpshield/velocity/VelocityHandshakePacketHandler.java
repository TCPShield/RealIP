package net.tcpshield.tcpshield.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.velocity.impl.VelocityConfigImpl;
import net.tcpshield.tcpshield.velocity.impl.VelocityPacketImpl;
import net.tcpshield.tcpshield.velocity.impl.VelocityPlayerImpl;

import java.io.File;
import java.util.logging.Logger;

public class VelocityHandshakePacketHandler {

    private final HandshakePacketHandler handshakePacketHandler;

    public VelocityHandshakePacketHandler(Logger logger, File dataFolder) {
        this.handshakePacketHandler = new HandshakePacketHandler(logger, new VelocityConfigImpl(dataFolder));
    }

    @Subscribe
    public void onHandshake(ConnectionHandshakeEvent e) {
        InboundConnection connection = e.getConnection();
        handleEvent(connection);
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent e) {
        InboundConnection connection = e.getConnection();
        handleEvent(connection);
    }

    private void handleEvent(InboundConnection connection) {
        IPlayer player = new VelocityPlayerImpl(connection);
        IPacket packet = new VelocityPacketImpl(connection);

        handshakePacketHandler.onHandshake(packet, player);
    }

}
