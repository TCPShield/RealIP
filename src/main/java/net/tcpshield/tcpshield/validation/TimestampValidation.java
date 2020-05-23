package net.tcpshield.tcpshield.validation;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import net.tcpshield.tcpshield.validation.impl.DisabledTimestampValidatorImpl;
import net.tcpshield.tcpshield.validation.impl.HTPDateTimestampValidatorImpl;
import net.tcpshield.tcpshield.validation.impl.SystemTimeTimestampValidatorImpl;

public class TimestampValidation {

    private final ITimestampValidator timestampValidator;

    public TimestampValidation(TCPShieldConfig config) {
        switch (config.getTimestampValidationMode().toLowerCase()) {
            case "system":
                this.timestampValidator = new SystemTimeTimestampValidatorImpl();
                break;
            case "off":
                this.timestampValidator = new DisabledTimestampValidatorImpl();
                break;
            case "htpdate":
            default:
                this.timestampValidator = new HTPDateTimestampValidatorImpl();
        }
    }

    public boolean checkTimestamp(long timestamp) {
        return timestampValidator.validateTimestamp(timestamp);
    }

    public long getTime() {
        return timestampValidator.getTime();
    }

}
