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


	private static Class<?> CONNECTED_PLAYER_CONNECTION_CLASS;

	static {
		try {
			CONNECTED_PLAYER_CONNECTION_CLASS = Class.forName("com.velocitypowered.proxy.connection.client.ConnectedPlayer");
		} catch (Exception e) {
			// ignore for old velocity versions
		}
	}

	public VelocityHandshakeHandler(TCPShieldPlugin plugin) {
		this.plugin = plugin;
	}

	// Turns out this event sometimes passes erroneous hostnames
	// which have the null bytes terminated as FML data which causes
	// issues with the verification process.
	@Subscribe(order = PostOrder.FIRST)
	public void onPreLogin(PreLoginEvent e) {
		if (!this.plugin.getConfigProvider().handlePreLoginEvent()) {
			return;
		}

		InboundConnection connection = e.getConnection();
		handleEvent(connection, "onPreLogin");
	}

	@Subscribe(order = PostOrder.FIRST)
	public void onHandshake(ConnectionHandshakeEvent e) {
		InboundConnection connection = e.getConnection();
		handleEvent(connection, "onHandshake");
	}

	@Subscribe(order = PostOrder.FIRST)
	public void onProxyPing(ProxyPingEvent e) {
		InboundConnection connection = e.getConnection();

		if (connection.getClass() == CONNECTED_PLAYER_CONNECTION_CLASS) {
			// new ServerData (0x42) packet on connect, we don't care about it
			return;
		}

		handleEvent(connection, "onProxyPing");
	}

	private void handleEvent(InboundConnection connection, String debugSource) {
		VelocityPlayer player = new VelocityPlayer(connection);
		if (player.getConnectionType() == VelocityPlayer.ConnectionType.LEGACY) {
			player.disconnect();
			return;
		}

		VelocityPacket packet = new VelocityPacket(connection);

		this.plugin.getDebugger().warn("Velocity: " + debugSource + " Raw player hostname: " + packet.getPayloadString());

		try {
			plugin.getPacketHandler().handleHandshake(packet, player);
		} catch (HandshakeException exception) {
			plugin.getDebugger().exception(exception);
		}
	}

}
