package net.tcpshield.tcpshield.bungee.impl;

import io.netty.channel.AbstractChannel;
import net.md_5.bungee.api.connection.PendingConnection;
import net.tcpshield.tcpshield.ReflectionUtils;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.exception.IPModificationFailureException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

public class BungeePlayerImpl implements IPlayer {

    private final PendingConnection pendingConnection;
    private String ip;

    public BungeePlayerImpl(PendingConnection pendingConnection) {
        this.pendingConnection = pendingConnection;
        this.ip = pendingConnection.getAddress().getAddress().getHostAddress();
    }

    @Override
    public String getUUID() {
        UUID uuid = pendingConnection.getUniqueId();
        if (uuid == null) return "Unknown";

        return uuid.toString();
    }

    @Override
    public String getName() {
        return pendingConnection.getName();
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public void setIP(InetSocketAddress ip) throws IPModificationFailureException {
        this.ip = ip.getAddress().getHostAddress();

        try {
            Object channelWrapper = ReflectionUtils.getObjectInPrivateField(pendingConnection, "ch");
            Object channel = ReflectionUtils.getObjectInPrivateField(channelWrapper, "ch");

            ReflectionUtils.setFinalField(channelWrapper, ReflectionUtils.searchFieldByClass(channelWrapper.getClass(), SocketAddress.class), ip);
            ReflectionUtils.setFinalField(channel, ReflectionUtils.getPrivateField(AbstractChannel.class, "remoteAddress"), ip);
            ReflectionUtils.setFinalField(channel, ReflectionUtils.getPrivateField(AbstractChannel.class, "localAddress"), ip);
        } catch (Exception e) {
            throw new IPModificationFailureException(e);
        }
    }

    @Override
    public void disconnect() {
        pendingConnection.disconnect();
    }
}
