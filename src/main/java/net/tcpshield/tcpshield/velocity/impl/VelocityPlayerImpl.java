package net.tcpshield.tcpshield.velocity.impl;

import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.ReflectionUtils;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.exception.IPModificationFailureException;
import net.tcpshield.tcpshield.exception.TCPShieldInitializationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class VelocityPlayerImpl implements IPlayer {

    private static final Field MINECRAFT_CONNECTION_FIELD;
    private static final Field LEGACY_MINECRAFT_CONNECTION_FIELD;
    private static final Field REMOTE_ADDRESS_FIELD;
    private static final Method CLOSE_CHANNEL_METHOD;

    static {
        try {
            MINECRAFT_CONNECTION_FIELD = ReflectionUtils.getPrivateField(Class.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection"), "connection");
            LEGACY_MINECRAFT_CONNECTION_FIELD = ReflectionUtils.getPrivateField(Class.forName("com.velocitypowered.proxy.connection.client.HandshakeSessionHandler$LegacyInboundConnection"), "connection");

            Class<?> minecraftConnection = Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection");
            REMOTE_ADDRESS_FIELD = ReflectionUtils.getPrivateField(minecraftConnection, "remoteAddress");
            CLOSE_CHANNEL_METHOD = minecraftConnection.getMethod("close");
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            throw new TCPShieldInitializationException(e);
        }
    }

    private final InboundConnection inboundConnection;
    private final boolean legacy;
    private String ip;

    public VelocityPlayerImpl(InboundConnection inboundConnection) {
        this.inboundConnection = inboundConnection;
        this.legacy = inboundConnection.getProtocolVersion().isUnknown() || inboundConnection.getProtocolVersion().isLegacy();
        this.ip = inboundConnection.getRemoteAddress().getAddress().getHostAddress();
    }

    @Override
    public String getUUID() {
        return "unknown"; // not supported
    }

    @Override
    public String getName() {
        return "unknown"; // not supported
    }

    @Override
    public String getIP() {
        return ip;
    }

    public boolean isLegacy() {
        return legacy;
    }

    @Override
    public void setIP(InetSocketAddress ip) throws IPModificationFailureException {
        this.ip = ip.getAddress().getHostAddress();

        try {
            Object minecraftConnection = MINECRAFT_CONNECTION_FIELD.get(inboundConnection);
            REMOTE_ADDRESS_FIELD.set(minecraftConnection, ip);
        } catch (Exception e) {
            throw new IPModificationFailureException(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            Object minecraftConnection = legacy ? LEGACY_MINECRAFT_CONNECTION_FIELD.get(inboundConnection) : MINECRAFT_CONNECTION_FIELD.get(inboundConnection);

            CLOSE_CHANNEL_METHOD.invoke(minecraftConnection);
        } catch (Exception e) {
            throw new RuntimeException(e); // pass exception on
        }
    }
}
