package net.tcpshield.tcpshield.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.tcpshield.tcpshield.TCPShieldPacketHandler;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.fabric.impl.FabricConfig;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

public class TCPShieldFabric implements DedicatedServerModInitializer, TCPShieldPlugin {

    public static final Logger LOGGER = Logger.getLogger("TCPShield");
    public static TCPShieldPacketHandler packetHandler;
    private FabricConfig config;
    private Debugger debugger;

    @Override
    public void onInitializeServer() {
        try {
            config = new FabricConfig();
            debugger = Debugger.createDebugger(this);
            packetHandler = new TCPShieldPacketHandler(this);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException exception) {
            exception.printStackTrace();
        }

        LOGGER.info("TCPShield has been loaded.");
    }

    @Override
    public ConfigProvider getConfigProvider() {
        return config;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public TCPShieldPacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public Debugger getDebugger() {
        return debugger;
    }
}
