package net.tcpshield.tcpshield.impl;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

public class TestConfigImpl extends TCPShieldConfig {

    public TestConfigImpl(String timestampValidationMode) {
        this.timestampValidationMode = timestampValidationMode;
        this.debug = true;
        this.onlyProxy = true;
    }
}
