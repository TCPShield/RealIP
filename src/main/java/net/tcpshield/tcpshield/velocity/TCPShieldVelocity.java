package net.tcpshield.tcpshield.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.tcpshield.tcpshield.TCPShieldPacketHandler;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.geyser.GeyserUtils;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import net.tcpshield.tcpshield.velocity.handler.VelocityHandshakeHandler;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * The entry point for Velocity servers
 */
@Plugin(
	id = "tcpshield",
	name = "TCPShield",
	description = "TCPShield IP parsing capabilities for Velocity"
)
public class TCPShieldVelocity implements TCPShieldPlugin {

	private final ProxyServer server;
	private final Logger logger;
	private final Path dataFolder;

	private ConfigProvider configProvider;
	private TCPShieldPacketHandler packetHandler;
	private Debugger debugger;

	@Inject
	public TCPShieldVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
		this.server = server;
		this.logger = logger;
		this.dataFolder = dataFolder;
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent e) {
		try {
			configProvider = new VelocityConfig(dataFolder.toFile(), this);
			debugger = Debugger.createDebugger(this);
			packetHandler = new TCPShieldPacketHandler(this);

			server.getEventManager().register(this, new VelocityHandshakeHandler(this));

			GeyserUtils.initGeyser(this, configProvider);

			initialization();
		} catch (Exception exception) {
			throw new InitializationException(exception);
		}
	}

	/*
	 * The provider's base methods
	 */

	@Override
	public ConfigProvider getConfigProvider() {
		return configProvider;
	}

	@Override
	public Logger getLogger() {
		return logger;
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
