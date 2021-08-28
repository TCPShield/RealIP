package net.tcpshield.tcpshield.velocity.handler;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;

/**
 * The handshake handler for Velocity
 */
public class VelocityHandshakeHandler {

	private final TCPShieldPlugin plugin;

	public VelocityHandshakeHandler(TCPShieldPlugin plugin) {
		this.plugin = plugin;
	}

	@Subscribe(order = PostOrder.FIRST)
	public void onPreLogin(PreLoginEvent e) {
		InboundConnection connection = e.getConnection();
		handleEvent(connection);
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
		VelocityPlayer player = new VelocityPlayer(connection);
		if (player.isLegacy()) {
			player.disconnect();
			return;
		}

		VelocityPacket packet = new VelocityPacket(connection);

		try {
			plugin.getPacketHandler().handleHandshake(packet, player);
		} catch (HandshakeException exception) {
			plugin.getDebugger().exception(exception);
		}
	}

}
