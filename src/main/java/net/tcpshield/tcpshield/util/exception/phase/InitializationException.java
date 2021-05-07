package net.tcpshield.tcpshield.util.exception.phase;

import net.tcpshield.tcpshield.util.exception.TCPShieldException;

/**
 * An exception thrown during the initialization phase of TCPShield
 */
public class InitializationException extends TCPShieldException {

	public InitializationException(Throwable throwable) {
		super("An exception occured during the initalization process", throwable);
	}


	public InitializationException(String message) {
		super(message);
	}


	public InitializationException(String message, Throwable throwable) {
		super(message, throwable);
	}


	public InitializationException() {
		super();
	}

}