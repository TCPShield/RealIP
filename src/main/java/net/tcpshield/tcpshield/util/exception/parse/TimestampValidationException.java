package net.tcpshield.tcpshield.util.exception.parse;

import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;
import net.tcpshield.tcpshield.util.validation.timestamp.TimestampValidator;

/**
 * An exception thrown when a handshake packet failed timestamp validation
 */
public class TimestampValidationException extends HandshakeException {

	private final TimestampValidator timestampValidator;
	private final long timestamp;

	public TimestampValidationException(TimestampValidator timestampValidator, long timestamp) {
		super();

		this.timestampValidator = timestampValidator;
		this.timestamp = timestamp;
	}


	public TimestampValidator getTimestampValidator() {
		return timestampValidator;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
