package net.tcpshield.tcpshield.velocity.handler;

import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.util.ReflectionUtil;
import net.tcpshield.tcpshield.util.exception.manipulate.PacketManipulationException;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import net.tcpshield.tcpshield.util.exception.phase.ReflectionException;

import java.lang.reflect.Field;

/**
 * A packet wrapper for Velocity
 */
public class VelocityPacket implements PacketProvider {

	/*
	 * Reflection objects needed for manipulation
	 */
	private static final Field HANDSHAKE_FIELD;
	private static final Field HOSTNAME_FIELD;
	private static final Field CLEANED_ADDRESS_FIELD;

	// new velocity support
	private static Class<?> LOGIN_INBOUND_CONNECTION_CLASS;
	private static Field LOGIN_INBOUND_CONNECTION_DELEGATE_FIELD;

	static {
		try {
			Class<?> inboundConnection = Class.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection");

			HANDSHAKE_FIELD = ReflectionUtil.getPrivateField(inboundConnection, "handshake");
			HOSTNAME_FIELD = ReflectionUtil.getPrivateField(Class.forName("com.velocitypowered.proxy.protocol.packet.Handshake"), "serverAddress");
			CLEANED_ADDRESS_FIELD = ReflectionUtil.getPrivateField(inboundConnection, "cleanedAddress");
		} catch (Exception e) {
			throw new InitializationException(new ReflectionException(e));
		}

		// LoginInboundConnection support
		try {
			LOGIN_INBOUND_CONNECTION_CLASS = Class.forName("com.velocitypowered.proxy.connection.client.LoginInboundConnection");
			LOGIN_INBOUND_CONNECTION_DELEGATE_FIELD = ReflectionUtil.getPrivateField(LOGIN_INBOUND_CONNECTION_CLASS, "delegate");
		} catch (Exception e) {
			// ignore for old versions of velocity
		}
	}


	private final InboundConnection inboundConnection;
	private final String rawPayload;

	public VelocityPacket(InboundConnection inboundConnection) {
		// support new velocity connection type
		if (inboundConnection.getClass() == LOGIN_INBOUND_CONNECTION_CLASS) {
			try {
				inboundConnection = (InboundConnection) LOGIN_INBOUND_CONNECTION_DELEGATE_FIELD.get(inboundConnection);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		this.inboundConnection = inboundConnection;
		try {
			this.rawPayload = (String) HOSTNAME_FIELD.get(HANDSHAKE_FIELD.get(inboundConnection));
		} catch (IllegalAccessException e) {
			throw new PacketManipulationException(e);
		}
	}


	@Override
	public String getPayloadString() {
		return rawPayload;
	}

	@Override
	public void setPacketHostname(String hostname) throws PacketManipulationException {
		try {
			ReflectionUtil.setFinalField(inboundConnection, CLEANED_ADDRESS_FIELD, cleanAddress(hostname));

			Object handshake = HANDSHAKE_FIELD.get(inboundConnection);
			HOSTNAME_FIELD.set(handshake, hostname);
		} catch (Exception e) {
			throw new PacketManipulationException(e);
		}
	}

	/**
	 * Adapted from https://github.com/VelocityPowered/Velocity/blob/17e6944daea8130e03903ccdfbf63f111c573849/proxy/src/main/java/com/velocitypowered/proxy/connection/client/HandshakeSessionHandler.java
	 */
	private String cleanAddress(String hostname) {
		// Clean out any anything after any zero bytes (this includes BungeeCord forwarding and the
		// legacy Forge handshake indicator).
		String cleaned = hostname;
		int zeroIdx = cleaned.indexOf('\0');
		if (zeroIdx > -1) {
			cleaned = hostname.substring(0, zeroIdx);
		}

		// If we connect through an SRV record, there will be a period at the end (DNS usually elides
		// this ending octet).
		if (!cleaned.isEmpty() && cleaned.charAt(cleaned.length() - 1) == '.') {
			cleaned = cleaned.substring(0, cleaned.length() - 1);
		}
		return cleaned;
	}

}
