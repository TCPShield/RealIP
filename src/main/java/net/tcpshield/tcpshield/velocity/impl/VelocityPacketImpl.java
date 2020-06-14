package net.tcpshield.tcpshield.velocity.impl;

import com.velocitypowered.api.proxy.InboundConnection;
import net.tcpshield.tcpshield.ReflectionUtils;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.exception.TCPShieldInitializationException;

import java.lang.reflect.Field;

public class VelocityPacketImpl implements IPacket {

    private static final Field HANDSHAKE_FIELD;
    private static final Field HOSTNAME_FIELD;

    static {
        try {
            HANDSHAKE_FIELD = ReflectionUtils.getPrivateField(Class.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection"), "handshake");
            HOSTNAME_FIELD = ReflectionUtils.getPrivateField(Class.forName("com.velocitypowered.proxy.protocol.packet.Handshake"), "serverAddress");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new TCPShieldInitializationException(e);
        }
    }

    private final InboundConnection inboundConnection;
    private final String rawPayload;

    public VelocityPacketImpl(InboundConnection inboundConnection) {
        this.inboundConnection = inboundConnection;
        try {
            this.rawPayload = (String) HOSTNAME_FIELD.get(HANDSHAKE_FIELD.get(inboundConnection));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // pass on
        }
    }

    @Override
    public String getRawPayload() {
        return rawPayload;
    }

    @Override
    public void modifyOriginalPacket(String hostname) throws Exception {
        Object handshake = HANDSHAKE_FIELD.get(inboundConnection);
        HOSTNAME_FIELD.set(handshake, hostname);
    }
}
