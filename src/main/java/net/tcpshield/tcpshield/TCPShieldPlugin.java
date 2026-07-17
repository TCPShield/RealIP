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
			String[] versionParts = jvmVersion.split("\\.");
			int baseVersion = Integer.parseInt(versionParts[0]);
			if (baseVersion < 25)
				this.getDebugger().warn("This TCPShield Folia fork requires Java 25 or newer. Your version: Java %s", jvmVersion);
		} catch (Throwable t) {
			this.getDebugger().error("Failed to check java version for string: " + jvmVersion);
		}
	}

}
