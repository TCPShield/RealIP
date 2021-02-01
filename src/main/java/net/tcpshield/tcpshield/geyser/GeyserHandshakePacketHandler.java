package net.tcpshield.tcpshield.geyser;

import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.geyser.impl.GeyserPacketImpl;
import net.tcpshield.tcpshield.geyser.impl.GeyserPlayerImpl;
import org.geysermc.floodgate.api.InstanceHolder;

public class GeyserHandshakePacketHandler {

    private final HandshakePacketHandler handshakePacketHandler;

    public GeyserHandshakePacketHandler(HandshakePacketHandler handshakePacketHandler) {
        this.handshakePacketHandler = handshakePacketHandler;
    }

    public void init() {
        InstanceHolder.getHandshakeHandlers().addHandshakeHandler(data -> {
            System.out.println("handshake handler");
            System.out.println("data = " + data);

            // In case the connection is a Bedrock connection, we don't bother
            if (data.getBedrockData() != null) return;
            System.out.println("not bedrock");


            IPlayer player = new GeyserPlayerImpl(data);
            IPacket packet = new GeyserPacketImpl(data);

            handshakePacketHandler.onHandshake(packet, player);
        });
    }
}