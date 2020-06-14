package net.tcpshield.tcpshield.exception;

import lombok.Getter;

public class InvalidTimestampException extends SigningVerificationFailureException {

    @Getter private final long timestamp;
    @Getter private final long currentTime;

    public InvalidTimestampException(long timestamp, long currentTime) {
        this.timestamp = timestamp;
        this.currentTime = currentTime;
    }

}
