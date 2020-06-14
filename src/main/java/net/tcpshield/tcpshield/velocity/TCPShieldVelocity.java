package net.tcpshield.tcpshield.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
    id = "tcpshield",
    name = "TCPShield",
    description = "TCPShield IP parsing capabilities for Velocity"
)
public class TCPShieldVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    @Inject
    public TCPShieldVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        server.getEventManager().register(this, new VelocityHandshakePacketHandler(logger, dataFolder.toFile()));
    }

}