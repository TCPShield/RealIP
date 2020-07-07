package net.tcpshield.tcpshield.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.velocity.event.InboundConnectionCheckEvent;
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

    @Subscribe(order = PostOrder.FIRST)
    public void onHandshake(ConnectionHandshakeEvent event) {
        this.runEvent(event.getConnection());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onProxyPing(ProxyPingEvent event) {
        this.runEvent(event.getConnection());
    }

    private void runEvent(InboundConnection connection) {
        TCPShieldVelocity.getInstance().getServer().getEventManager()
                .fireAndForget(new InboundConnectionCheckEvent(connection, this.handleEvent(connection)));
    }

    private boolean handleEvent(InboundConnection connection) {
        VelocityPlayerImpl player = new VelocityPlayerImpl(connection);

        if (!player.isLegacy())
            return this.handshakePacketHandler.onHandshake(new VelocityPacketImpl(connection), player);

        player.disconnect();
        return false;
    }
}
