package net.tcpshield.tcpshield.bukkit;

import net.tcpshield.tcpshield.TCPShieldPacketHandler;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.bukkit.paper.BukkitPaper;
import net.tcpshield.tcpshield.bukkit.protocollib.BukkitProtocolLib;
import net.tcpshield.tcpshield.bukkit.provider.BukkitImplProvider;
import net.tcpshield.tcpshield.geyser.GeyserUtils;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The entry point for Bukkit servers
 */
public class TCPShieldBukkit extends JavaPlugin implements TCPShieldPlugin {

	private ConfigProvider configProvider;
	private TCPShieldPacketHandler packetHandler;
	private Debugger debugger;

	private BukkitImplProvider bukkitImpl;

	@Override
	public void onEnable() {
		try {
			configProvider = new BukkitConfig(this);
			debugger = Debugger.createDebugger(this);
			packetHandler = new TCPShieldPacketHandler(this);

			if (BukkitImplProvider.hasPaperEvent())
				bukkitImpl = new BukkitPaper(this);
			else {
				if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
					getLogger().severe("TCPShield not loading because ProtocolLib is not installed. Either use Paper to enable native compatibility or install ProtocolLib.");
					return;
				}

				bukkitImpl = new BukkitProtocolLib(this);
			}

			bukkitImpl.load();

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
	public TCPShieldPacketHandler getPacketHandler() {
		return packetHandler;
	}

	@Override
	public Debugger getDebugger() {
		return debugger;
	}

	@Override
	public ConfigProvider getConfigProvider() {
		return configProvider;
	}

}
