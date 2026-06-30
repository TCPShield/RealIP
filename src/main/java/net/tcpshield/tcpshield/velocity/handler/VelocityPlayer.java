package net.tcpshield.tcpshield.velocity.handler;

import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.ReflectionUtil;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import net.tcpshield.tcpshield.util.exception.phase.ReflectionException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * A player wrapper for Velocity
 */
public class VelocityPlayer implements PlayerProvider {

    private static final Class<?> INITIAL_INBOUND_CONNECTION_CLASS;
    private static final Class<?> LEGACY_INBOUND_CONNECTION_CLASS;
    private static final Field MINECRAFT_CONNECTION_FIELD;
    private static final Field LEGACY_MINECRAFT_CONNECTION_FIELD;
    private static final Field REMOTE_ADDRESS_FIELD;
    private static final Method CLOSE_CHANNEL_METHOD;

    private static Class<?> LOGIN_INBOUND_CONNECTION_CLASS;
    private static Field LOGIN_INBOUND_CONNECTION_DELEGATE_FIELD;

    static {
        try {
            INITIAL_INBOUND_CONNECTION_CLASS = Class.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection");
            MINECRAFT_CONNECTION_FIELD = ReflectionUtil.getPrivateField(INITIAL_INBOUND_CONNECTION_CLASS, "connection");
            LEGACY_INBOUND_CONNECTION_CLASS = Class.forName("com.velocitypowered.proxy.connection.client.HandshakeSessionHandler$LegacyInboundConnection");
            LEGACY_MINECRAFT_CONNECTION_FIELD = ReflectionUtil.getPrivateField(LEGACY_INBOUND_CONNECTION_CLASS, "connection");

            Class<?> minecraftConnection = Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection");
            REMOTE_ADDRESS_FIELD = ReflectionUtil.getPrivateField(minecraftConnection, "remoteAddress");
            CLOSE_CHANNEL_METHOD = minecraftConnection.getMethod("close");
        } catch (Exception e) {
            throw new InitializationException(new ReflectionException(e));
        }

        try {
            LOGIN_INBOUND_CONNECTION_CLASS = Class.forName("com.velocitypowered.proxy.connection.client.LoginInboundConnection");
            LOGIN_INBOUND_CONNECTION_DELEGATE_FIELD = ReflectionUtil.getPrivateField(LOGIN_INBOUND_CONNECTION_CLASS, "delegate");
        } catch (Exception e) {
            // ignore for old versions of velocity
        }
    }

    private final InboundConnection inboundConnection;
    private final ConnectionType connectionType;
    private String ip;

    public VelocityPlayer(InboundConnection inboundConnection) {
        this.inboundConnection = inboundConnection;
        this.ip = inboundConnection.getRemoteAddress().getAddress().getHostAddress();

        if (this.inboundConnection.getClass() == INITIAL_INBOUND_CONNECTION_CLASS) {
            this.connectionType = ConnectionType.INITIAL_INBOUND;
        } else if (this.inboundConnection.getClass() == LOGIN_INBOUND_CONNECTION_CLASS) {
            this.connectionType = ConnectionType.LOGIN_INBOUND;
        } else if (this.inboundConnection.getClass() == LEGACY_INBOUND_CONNECTION_CLASS) {
            this.connectionType = ConnectionType.LEGACY;
        } else {
            // Unrecognized InboundConnection implementation (e.g. Geyser's
            // GeyserVelocityPingPassthrough$GeyserInboundConnection). These are not
            // real player connections we can manipulate, so they're ignored.
            this.connectionType = ConnectionType.UNKNOWN;
        }
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
        return ip;
    }

    @Override
    public void setIP(InetSocketAddress ip) throws PlayerManipulationException {
        try {
            this.ip = ip.getAddress().getHostAddress();

            Object minecraftConnection = this.getMinecraftConnection();
            REMOTE_ADDRESS_FIELD.set(minecraftConnection, ip);
        } catch (Exception e) {
            throw new PlayerManipulationException(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            Object minecraftConnection = this.getMinecraftConnection();

            CLOSE_CHANNEL_METHOD.invoke(minecraftConnection);
        } catch (Exception e) {
            throw new PlayerManipulationException(e);
        }
    }

    private Object getMinecraftConnection() {
        try {
            switch (this.connectionType) {
                case LEGACY:
                    return LEGACY_MINECRAFT_CONNECTION_FIELD.get(inboundConnection);
                case INITIAL_INBOUND:
                    return MINECRAFT_CONNECTION_FIELD.get(inboundConnection);
                case LOGIN_INBOUND: {
                    Object initialInboundConnection = LOGIN_INBOUND_CONNECTION_DELEGATE_FIELD.get(this.inboundConnection);
                    return MINECRAFT_CONNECTION_FIELD.get(initialInboundConnection);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    enum ConnectionType {
        LOGIN_INBOUND, INITIAL_INBOUND, LEGACY, UNKNOWN
    }

}
