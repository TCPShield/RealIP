package net.tcpshield.tcpshield.validation;

public interface ITimestampValidator {

    boolean validateTimestamp(long checkedTimestamp);

    default long getTime() {
        return System.currentTimeMillis() / 1000;
    }

}
