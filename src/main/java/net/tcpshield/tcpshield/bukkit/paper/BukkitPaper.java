package net.tcpshield.tcpshield.bukkit.paper;

import net.tcpshield.tcpshield.bukkit.TCPShieldBukkit;
import net.tcpshield.tcpshield.bukkit.paper.handler.PaperHandshakeHandler;
import net.tcpshield.tcpshield.bukkit.provider.BukkitImplProvider;

/**
 * Bukkit provider for the PaperSpigot implementation of TCPShield
 */
public class BukkitPaper extends BukkitImplProvider {

	public BukkitPaper(TCPShieldBukkit bukkitPlugin) {
		super(bukkitPlugin);
	}


	@Override
	public void load() {
		PaperHandshakeHandler packetHandler = new PaperHandshakeHandler(this);
		getPlugin().getServer().getPluginManager().registerEvents(packetHandler, getPlugin());
	}

}
