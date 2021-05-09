package net.tcpshield.tcpshield.util.exception.phase;

import net.tcpshield.tcpshield.util.exception.TCPShieldException;

/**
 * An exception thrown during the CIDR process of TCPShield
 */
public class CIDRException extends TCPShieldException {

	public CIDRException(Throwable throwable) {
		super("An exception occured during the CIDR process", throwable);
	}


	public CIDRException(String message) {
		super(message);
	}


	public CIDRException(String message, Throwable throwable) {
		super(message, throwable);
	}


	public CIDRException() {
		super();
	}

}
