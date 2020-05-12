package net.tcpshield.tcpshield.exception;

public class InvalidTimestampException extends SigningVerificationFailureException {

    private final long timestamp;
    private final long currentTime;

    public InvalidTimestampException(long timestamp, long currentTime) {
        this.timestamp = timestamp;
        this.currentTime = currentTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCurrentTime() {
        return currentTime;
    }
}
