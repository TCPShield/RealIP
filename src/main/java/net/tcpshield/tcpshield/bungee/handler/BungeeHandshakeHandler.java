package net.tcpshield.tcpshield.bungee.handler;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;

/**
 * The handshake handler for Bungee
 */
public class BungeeHandshakeHandler implements Listener {

	private final TCPShieldPlugin plugin;

	public BungeeHandshakeHandler(TCPShieldPlugin plugin) {
		this.plugin = plugin;
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onProxyPingEvent(ProxyPingEvent e) {
		PendingConnection connection = e.getConnection();
		if (!connection.isLegacy())
			return;

		connection.disconnect();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerHandshake(PlayerHandshakeEvent e) {
		BungeePacket packet = new BungeePacket(e.getHandshake(), e.getConnection());
		BungeePlayer player = new BungeePlayer(e.getConnection());

		this.plugin.getDebugger().warn("BungeeCord: Raw player hostname: " + packet.getPayloadString());

		try {
			plugin.getPacketHandler().handleHandshake(packet, player);
		} catch (HandshakeException exception) {
			plugin.getDebugger().exception(exception);
		}
	}

}
