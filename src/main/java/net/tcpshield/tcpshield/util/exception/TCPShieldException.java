package net.tcpshield.tcpshield.util.exception;

/**
 * Outline for TCPShield exceptions
 */
public abstract class TCPShieldException extends RuntimeException {

	public TCPShieldException(Throwable throwable) {
		super(throwable);
	}


	public TCPShieldException(String message) {
		super(message);
	}


	public TCPShieldException(String message, Throwable throwable) {
		super(message, throwable);
	}


	public TCPShieldException() {
		super();
	}

}
