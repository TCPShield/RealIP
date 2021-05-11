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
		ProtocolLibHandshakeHandler packetHandler = new ProtocolLibHandshakeHandler(this);
		com.comphenix.protocol.ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
	}

}
