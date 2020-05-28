package net.tcpshield.tcpshield.impl;

import net.tcpshield.tcpshield.abstraction.IPlayer;

import java.net.InetSocketAddress;

public class TestPlayerImpl implements IPlayer {

    private InetSocketAddress ip;
    private boolean connected;

    public TestPlayerImpl() {
        this.connected = true;
    }

    @Override
    public String getUUID() {
        return "uuid";
    }

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public String getIP() {
        if (ip == null) return "ip";

        return ip.getAddress().getHostAddress();
    }

    @Override
    public void setIP(InetSocketAddress ip) {
        this.ip = ip;
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
