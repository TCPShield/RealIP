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


	/**
	 * For exceptions raised per rejected connection, where the throw site is fixed
	 * and capturing a stack trace is pure overhead on the network threads.
	 *
	 * @param writableStackTrace Whether to capture a stack trace
	 */
	protected TCPShieldException(String message, Throwable throwable, boolean writableStackTrace) {
		super(message, throwable, true, writableStackTrace);
	}

}
