package net.tcpshield.tcpshield.bungee;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.tcpshield.tcpshield.TCPShieldPacketHandler;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.bungee.handler.BungeeHandshakeHandler;
import net.tcpshield.tcpshield.geyser.GeyserUtils;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;

/**
 * The entry point for Bungee servers
 */
public class TCPShieldBungee extends Plugin implements TCPShieldPlugin, Listener {

	private ConfigProvider configProvider;
	private TCPShieldPacketHandler packetHandler;
	private Debugger debugger;

	@Override
	public void onEnable() {
		try {
			configProvider = new BungeeConfig(this);
			debugger = Debugger.createDebugger(this);
			packetHandler = new TCPShieldPacketHandler(this);

			PluginManager pluginManager = this.getProxy().getPluginManager();
			pluginManager.registerListener(this, new BungeeHandshakeHandler(this));

			GeyserUtils.initGeyser(this, configProvider);

			initialization();
		} catch (Exception e) {
			throw new InitializationException(e);
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
	public TCPShieldPacketHandler getPacketHandler() {
		return packetHandler;
	}

	@Override
	public Debugger getDebugger() {
		return debugger;
	}

}
