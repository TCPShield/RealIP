package net.tcpshield.tcpshield.impl;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

import java.io.File;

public class TestConfigImpl extends TCPShieldConfig {

    public TestConfigImpl() {
        this.ipWhitelistFolder = new File("ip-whitelist");
        this.debug = true;
        this.onlyProxy = true;
    }
}
