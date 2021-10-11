package net.tcpshield.tcpshield.bungee.handler;

import net.md_5.bungee.api.connection.PendingConnection;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.ReflectionUtil;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

/**
 * A player wrapper for Bungee
 */
public class BungeePlayer implements PlayerProvider {

	private final PendingConnection pendingConnection;
	private String ip;

	public BungeePlayer(PendingConnection pendingConnection) {
		this.pendingConnection = pendingConnection;
		this.ip = pendingConnection.getAddress().getAddress().getHostAddress();
	}


	/**
	 * Trys to grab the UUID of the handshake
	 *
	 * @return If found, the corrosponding uuid, if not, unknown
	 */
	@Override
	public String getUUID() {
		UUID uuid = pendingConnection.getUniqueId();
		if (uuid == null)
			return "unknown";

		return uuid.toString();
	}

	@Override
	public String getName() {
		return pendingConnection.getName();
	}

	@Override
	public String getIP() {
		return ip;
	}

	@Override
	public void setIP(InetSocketAddress ip) throws PlayerManipulationException {
		try {
			this.ip = ip.getAddress().getHostAddress();

			Object channelWrapper = ReflectionUtil.getObjectInPrivateField(pendingConnection, "ch");
			Object channel = ReflectionUtil.getObjectInPrivateField(channelWrapper, "ch");

			try {
				Field socketAddressField = ReflectionUtil.searchFieldByClass(channelWrapper.getClass(), SocketAddress.class);
				ReflectionUtil.setFinalField(channelWrapper, socketAddressField, ip);
			} catch (IllegalArgumentException ignored) {
				// Some BungeeCord versions, notably those on 1.7 (e.g. zBungeeCord) don't have an SocketAddress field in the ChannelWrapper class
			}

			ReflectionUtil.setFinalField(channel, ReflectionUtil.getPrivateField(channel.getClass(), "remoteAddress"), ip);
			try {
				ReflectionUtil.setFinalField(channel, ReflectionUtil.getPrivateField(channel.getClass(), "localAddress"), ip);
			} catch (Throwable t) {
				// ChannelWrapper doesn't have a localAddress
			}
		} catch (Exception e) {
			throw new PlayerManipulationException(e);
		}
	}

	@Override
	public void disconnect() {
		pendingConnection.disconnect();
	}

}
