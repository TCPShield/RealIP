package net.tcpshield.tcpshield.impl;

import net.tcpshield.tcpshield.abstraction.IConfig;

public class TestConfigImpl implements IConfig {
    @Override
    public boolean isOnlyProxy() {
        return true;
    }

    @Override
    public boolean isDebug() {
        return true;
    }
}
