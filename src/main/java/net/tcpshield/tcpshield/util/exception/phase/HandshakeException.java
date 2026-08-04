package net.tcpshield.tcpshield.util.exception.phase;

import net.tcpshield.tcpshield.util.exception.TCPShieldException;

/**
 * An exception thrown during the handshake handling of TCPShield.
 *
 * <p>Every throw site is a rejected connection and every catch site only forwards
 * the exception to the debugger, so these carry no stack trace: the frames are
 * always the same, while an attacker can otherwise force a stack walk per
 * connection. The rejection reason stays in the message and in the accompanying
 * debugger warning, which name the player, address and raw payload.
 */
public class HandshakeException extends TCPShieldException {

	public HandshakeException(Throwable throwable) {
		super("An exception occured during the handshake process", throwable, false);
	}


	public HandshakeException(String message) {
		super(message, null, false);
	}


	public HandshakeException(String message, Throwable throwable) {
		super(message, throwable, false);
	}


	public HandshakeException() {
		super(null, null, false);
	}

}
