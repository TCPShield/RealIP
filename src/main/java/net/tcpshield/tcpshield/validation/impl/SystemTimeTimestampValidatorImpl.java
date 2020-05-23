package net.tcpshield.tcpshield.validation.impl;

import net.tcpshield.tcpshield.Constants;
import net.tcpshield.tcpshield.validation.ITimestampValidator;

public class SystemTimeTimestampValidatorImpl implements ITimestampValidator {

    @Override
    public boolean validateTimestamp(long checkedTimestamp) {
        long currentTime = getTime();

        return checkedTimestamp >= (currentTime - Constants.MAX_TIME_DIFFERENCE) && checkedTimestamp <= (currentTime + Constants.MAX_TIME_DIFFERENCE);
    }
}
