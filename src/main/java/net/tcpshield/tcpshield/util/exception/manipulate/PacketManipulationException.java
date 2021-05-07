package net.tcpshield.tcpshield.util.exception.manipulate;

import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;

/**
 * An exception thrown during packet manipulation
 */
public class PacketManipulationException extends HandshakeException {

	public PacketManipulationException(Throwable throwable) {
		super(throwable);
	}


	public PacketManipulationException(String message) {
		super(message);
	}


	public PacketManipulationException(String message, Throwable throwable) {
		super(message, throwable);
	}


	public PacketManipulationException() {
		super();
	}

}
