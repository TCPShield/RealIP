package net.tcpshield.tcpshield.validation.impl;

import net.tcpshield.tcpshield.validation.ITimestampValidator;

import static net.tcpshield.tcpshield.Constants.MAX_TIME_DIFFERENCE;

public class SystemTimeTimestampValidatorImpl implements ITimestampValidator {

    @Override
    public boolean validateTimestamp(long checkedTimestamp) {
        long currentTime = getTime();

        return checkedTimestamp >= (currentTime - MAX_TIME_DIFFERENCE) && checkedTimestamp <= (currentTime + MAX_TIME_DIFFERENCE);
    }

}
