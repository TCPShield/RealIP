package net.tcpshield.tcpshield.bukkit;

import java.util.logging.Logger;
import net.tcpshield.tcpshield.TCPShieldPacketHandler;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.bukkit.paper.BukkitPaper;
import net.tcpshield.tcpshield.bukkit.protocollib.BukkitProtocolLib;
import net.tcpshield.tcpshield.bukkit.provider.BukkitImplProvider;
import net.tcpshield.tcpshield.geyser.GeyserUtils;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import org.bukkit.Bukkit;
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

			// check force plib option -> paper -> plib -> error
			if (this.configProvider.preferProtocolLib() && getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
				try {
					String[] protocolLibVersion = getServer().getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion().split("-")[0].split("\\.");
					int major = Integer.parseInt(protocolLibVersion[0]);
					int minor = Integer.parseInt(protocolLibVersion[1]);
					int patch = Integer.parseInt(protocolLibVersion[2]);

					String paperVersion = Bukkit.getServer().getMinecraftVersion();
					if (major <= 5 && minor <= 2 && patch <= 1 && (paperVersion.equals("1.20.5") || paperVersion.equals("1.20.6"))) {
						getLogger().severe("TCPShield is incompatible with ProtocolLib <= 5.2.1 on Paper 1.20.5/1.20.6 due to lack of support from ProtocolLib. Reverting to default Paper handler to prevent issues. This error can be avoided by disabling 'prefer-protocollib' in the config.");
						bukkitImpl = new BukkitPaper(this);
					} else {
						bukkitImpl = new BukkitProtocolLib(this);
					}
				} catch (Exception t) {
					getLogger().warning("Failed to check Paper or ProtocolLib version. This is not a critical error unless you are running Paper 1.20.5/1.20.6 with ProtocolLib version 5.2.1 or below.");
					getDebugger().exception(t);

					bukkitImpl = new BukkitProtocolLib(this);
				}
			} else if (BukkitImplProvider.hasPaperEvent()) {
				bukkitImpl = new BukkitPaper(this);
			} else if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
				bukkitImpl = new BukkitProtocolLib(this);
			} else {
				getLogger().severe("TCPShield not loading because ProtocolLib is not installed. Either use Paper to enable native compatibility or install ProtocolLib.");
				return;
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

	@Override
	public Logger getLogger() {
		return super.getLogger();
	}

}
