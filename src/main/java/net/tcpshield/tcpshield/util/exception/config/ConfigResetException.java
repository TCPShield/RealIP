package net.tcpshield.tcpshield.util.exception.config;

import net.tcpshield.tcpshield.util.exception.phase.ConfigException;

/**
 * An exception thrown during the resetting phase of the config process
 */
public class ConfigResetException extends ConfigException {

	public ConfigResetException(Throwable throwable) {
		super(throwable);
	}


	public ConfigResetException(String message) {
		super(message);
	}


	public ConfigResetException(String message, Throwable throwable) {
		super(message, throwable);
	}


	public ConfigResetException() {
		super();
	}

}