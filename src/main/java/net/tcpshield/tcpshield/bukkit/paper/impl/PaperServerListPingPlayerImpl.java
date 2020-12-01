package net.tcpshield.tcpshield.bukkit.paper.impl;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.tcpshield.tcpshield.abstraction.IPlayer;

import java.net.InetSocketAddress;

public class PaperServerListPingPlayerImpl implements IPlayer {

    private final PaperServerListPingEvent pingEvent;

    public PaperServerListPingPlayerImpl(PaperServerListPingEvent pingEvent) {
        this.pingEvent = pingEvent;
    }

    @Override
    public String getUUID() {
        return "unknown";
    }

    @Override
    public String getName() {
        return "unknown";
    }

    @Override
    public String getIP() {
        return pingEvent.getAddress().getHostAddress();
    }

    @Override
    public void setIP(InetSocketAddress ip) {
        // not necessary since this class is only for server list pings
    }

    @Override
    public void disconnect() {
        pingEvent.setCancelled(true);
    }
}
