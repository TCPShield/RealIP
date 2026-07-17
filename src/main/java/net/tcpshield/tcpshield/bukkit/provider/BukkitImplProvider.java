package net.tcpshield.tcpshield.bukkit.provider;

import net.tcpshield.tcpshield.bukkit.TCPShieldBukkit;

/**
 * A provider for Bukkit implementations of TCPShield
 */
public abstract class BukkitImplProvider {

	private final TCPShieldBukkit bukkitPlugin;

	public BukkitImplProvider(TCPShieldBukkit bukkitPlugin) {
		this.bukkitPlugin = bukkitPlugin;
	}


	public TCPShieldBukkit getPlugin() {
		return bukkitPlugin;
	}

	/**
	 * Loads the Bukkit provider
	 */
	public abstract void load();


	/**
	 * Checks for the Paper handshake event class for the Paper implementation
	 * @return Boolean stating if the runtime has the proper Paper event
	 */
	public static boolean hasPaperEvent() {
		try {
			Class.forName("com.destroystokyo.paper.event.player.PlayerHandshakeEvent");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Checks whether the plugin is running on Folia. Folia must use Paper's
	 * native handshake event so packet processing stays independent of
	 * ProtocolLib's server-thread assumptions.
	 *
	 * @return whether Folia's regionized server implementation is present
	 */
	public static boolean isFolia() {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
