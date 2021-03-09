package net.tcpshield.tcpshield.fabric;

import net.fabricmc.api.ModInitializer;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.fabric.impl.FabricConfigImpl;

import java.util.logging.Logger;

public class TCPShieldFabric implements ModInitializer {

    public static final Logger LOGGER = Logger.getLogger("TCPShield");
    public static final HandshakePacketHandler PACKET_HANDLER =  new HandshakePacketHandler(LOGGER, new FabricConfigImpl());

    @Override
    public void onInitialize() {
        LOGGER.info("TCPShield has been loaded.");
    }
}
