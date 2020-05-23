package net.tcpshield.tcpshield.impl;

import net.tcpshield.tcpshield.abstraction.IConfig;

public class TestConfigImpl implements IConfig {

    private final boolean checkTimestamp;

    public TestConfigImpl(boolean checkTimestamp) {
        this.checkTimestamp = checkTimestamp;
    }

    @Override
    public boolean isOnlyProxy() {
        return true;
    }

    @Override
    public boolean checkTimestamp() {
        return checkTimestamp;
    }

    @Override
    public boolean isDebug() {
        return true;
    }
}
