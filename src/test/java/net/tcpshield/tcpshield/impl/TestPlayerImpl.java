package net.tcpshield.tcpshield.impl;

import net.tcpshield.tcpshield.abstraction.IPlayer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class TestPlayerImpl implements IPlayer {

    private InetAddress ip;
    private boolean connected;

    public TestPlayerImpl(String ip) {
        this.connected = true;
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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

        return ip.getHostAddress();
    }

    @Override
    public void setIP(InetSocketAddress ip) {
        this.ip = ip.getAddress();
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
