package net.tcpshield.tcpshield.util.exception.manipulate;

import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;

/**
 * An exception thrown during player manipulation
 */
public class PlayerManipulationException extends HandshakeException {

	public PlayerManipulationException(Throwable throwable) {
		super(throwable);
	}


	public PlayerManipulationException(String message) {
		super(message);
	}


	public PlayerManipulationException(String message, Throwable throwable) {
		super(message, throwable);
	}


	public PlayerManipulationException() {
		super();
	}

}
