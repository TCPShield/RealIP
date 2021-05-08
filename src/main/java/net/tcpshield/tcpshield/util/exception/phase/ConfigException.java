package net.tcpshield.tcpshield.util.exception.phase;

import net.tcpshield.tcpshield.util.exception.TCPShieldException;

/**
 * An exception thrown during the config loading, reloeading, saving, etc. process of TCPShield
 */
public class ConfigException extends TCPShieldException {

	public ConfigException(Throwable throwable) {
		super("An exception occured during the config process", throwable);
	}


	public ConfigException(String message) {
		super(message);
	}


	public ConfigException(String message, Throwable throwable) {
		super(message, throwable);
	}


	public ConfigException() {
		super();
	}

}
