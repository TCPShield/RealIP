package net.tcpshield.tcpshield.geyser;

import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

public class GeyserUtils {

    public static void initGeyserOrDefault(HandshakePacketHandler handshakePacketHandler, Runnable registrar) {
        if (!useGeyser(handshakePacketHandler.getConfig())) {
            registrar.run();
        } else {
            GeyserHandshakePacketHandler geyserHandler = new GeyserHandshakePacketHandler(handshakePacketHandler);
            geyserHandler.init();
        }
    }

    private static boolean useGeyser(TCPShieldConfig config) {
        if (!config.isGeyser()) return false;

        try {
            Class.forName("org.geysermc.floodgate.api.InstanceHolder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
