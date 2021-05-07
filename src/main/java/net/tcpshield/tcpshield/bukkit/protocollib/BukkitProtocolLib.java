package net.tcpshield.tcpshield.bukkit.protocollib;

import net.tcpshield.tcpshield.bukkit.TCPShieldBukkit;
import net.tcpshield.tcpshield.bukkit.protocollib.handler.ProtocolLibHandshakeHandler;
import net.tcpshield.tcpshield.bukkit.provider.BukkitImplProvider;

/**
 * Bukkit provider for the ProtocolLib implementation of TCPShield
 */
public class BukkitProtocolLib extends BukkitImplProvider {

	public BukkitProtocolLib(TCPShieldBukkit bukkitPlugin) {
		super(bukkitPlugin);
	}


	@Override
	public void load() {
		if (getPlugin().getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
			getPlugin().getLogger().severe("TCPShield not loading because ProtocolLib is not installed. Either use Paper to enable native compatibility or install ProtocolLib.");
			return;
		}

		ProtocolLibHandshakeHandler packetHandler = new ProtocolLibHandshakeHandler(this);
		com.comphenix.protocol.ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
	}

}
