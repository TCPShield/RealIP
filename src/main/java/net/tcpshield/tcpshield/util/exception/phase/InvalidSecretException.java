package net.tcpshield.tcpshield.util.exception.phase;

import net.tcpshield.tcpshield.util.exception.TCPShieldException;

public class InvalidSecretException extends TCPShieldException {

	// Raised per rejected Geyser connection; see HandshakeException for why these
	// do not capture a stack trace.

	public InvalidSecretException(Throwable throwable) {
		super("An invalid secret was provided during the geyser handshake process", throwable, false);
	}


	public InvalidSecretException(String message) {
		super(message, null, false);
	}


	public InvalidSecretException(String message, Throwable throwable) {
		super(message, throwable, false);
	}


	public InvalidSecretException() {
		super(null, null, false);
	}

}
