package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;

import java.util.logging.Logger;

/**
 * The base/provider for all entry points/main classes
 */
public interface TCPShieldPlugin {

	/**
	 * Gets the plugin's config provider
	 *
	 * @return The plugin's config provider
	 */
	ConfigProvider getConfigProvider();

	/**
	 * Gets the plugin's logger
	 *
	 * @return The plugin's logger
	 */
	Logger getLogger();

	/**
	 * Gets the packet handler
	 *
	 * @return The packet handler
	 */
	TCPShieldPacketHandler getPacketHandler();

	/**
	 * Gets the plugin's debugger
	 *
	 * @return The plugin's debugger
	 */
	Debugger getDebugger();


	/**
	 * Default initialization of TCPShield, called after interface defaults are set
	 */
	default void initialization() {
		String jvmVersion = System.getProperty("java.version");
		try {
			// Java 11 check/warning (Will eventually force Java 16 without warning)
			String[] versionParts = jvmVersion.split("\\.");
			int baseVersion = Integer.parseInt(versionParts[0]);
			if (baseVersion < 11) // Java 8, and below, starts with 1, but since we are using Java 11 we can ignore sub values
				this.getDebugger().warn("The Java version you are running is outdated for TCPShield and may cause issues. Update to atleast Java 11. Your version: Java %s", jvmVersion);
		} catch (Throwable t) {
			this.getDebugger().error("Failed to check java version for string: " + jvmVersion);
		}
	}

}
