package net.tcpshield.tcpshield.velocity.handler;

import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.ReflectionUtil;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import net.tcpshield.tcpshield.util.exception.phase.ReflectionException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * A player wrapper for Velocity
 */
public class VelocityPlayer implements PlayerProvider {

	/*
	 * Reflection objects needed for manipulation
	 */
	private static final Class<?> INITIAL_INBOUND_CONNECTION_CLASS;
	private static final Field MINECRAFT_CONNECTION_FIELD;
	private static final Field LEGACY_MINECRAFT_CONNECTION_FIELD;
	private static final Field REMOTE_ADDRESS_FIELD;
	private static final Method CLOSE_CHANNEL_METHOD;

	static {
		try {
			INITIAL_INBOUND_CONNECTION_CLASS = Class.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection");
			MINECRAFT_CONNECTION_FIELD = ReflectionUtil.getPrivateField(INITIAL_INBOUND_CONNECTION_CLASS, "connection");
			LEGACY_MINECRAFT_CONNECTION_FIELD = ReflectionUtil.getPrivateField(Class.forName("com.velocitypowered.proxy.connection.client.HandshakeSessionHandler$LegacyInboundConnection"), "connection");

			Class<?> minecraftConnection = Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection");
			REMOTE_ADDRESS_FIELD = ReflectionUtil.getPrivateField(minecraftConnection, "remoteAddress");
			CLOSE_CHANNEL_METHOD = minecraftConnection.getMethod("close");
		} catch (Exception e) {
			throw new InitializationException(new ReflectionException(e));
		}
	}


	private final InboundConnection inboundConnection;
	private final boolean legacy;
	private String ip;

	public VelocityPlayer(InboundConnection inboundConnection) {
		this.inboundConnection = inboundConnection;
		this.legacy = inboundConnection.getClass() != INITIAL_INBOUND_CONNECTION_CLASS;
		this.ip = inboundConnection.getRemoteAddress().getAddress().getHostAddress();
	}


	/**
	 * Unsupported with Velocity handshakes
	 * @return unknown
	 */
	@Override
	public String getUUID() {
		return "unknown";
	}

	/**
	 * Unsupported with Velocity handshakes
	 * @return unknown
	 */
	@Override
	public String getName() {
		return "unknown";
	}

	@Override
	public String getIP() {
		return ip;
	}

	public boolean isLegacy() {
		return legacy;
	}

	@Override
	public void setIP(InetSocketAddress ip) throws PlayerManipulationException {
		try {
			this.ip = ip.getAddress().getHostAddress();

			Object minecraftConnection = MINECRAFT_CONNECTION_FIELD.get(inboundConnection);
			REMOTE_ADDRESS_FIELD.set(minecraftConnection, ip);
		} catch (Exception e) {
			throw new PlayerManipulationException(e);
		}
	}

	@Override
	public void disconnect() {
		try {
			Object minecraftConnection = legacy ? LEGACY_MINECRAFT_CONNECTION_FIELD.get(inboundConnection) : MINECRAFT_CONNECTION_FIELD.get(inboundConnection);

			CLOSE_CHANNEL_METHOD.invoke(minecraftConnection);
		} catch (Exception e) {
			throw new PlayerManipulationException(e);
		}
	}

}
