package net.tcpshield.tcpshield.impl;

import net.tcpshield.tcpshield.abstraction.IPacket;

public class TestPacketImpl implements IPacket {

    private final String payload;
    private String hostname;

    public TestPacketImpl(String payload, String hostname) {
        this.payload = payload;
        this.hostname = hostname;
    }

    @Override
    public String getRawPayload() {
        return payload;
    }

    @Override
    public void modifyOriginalPacket(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }
}
