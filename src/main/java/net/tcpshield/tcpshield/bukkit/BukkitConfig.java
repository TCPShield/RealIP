package net.tcpshield.tcpshield.bukkit;

import net.tcpshield.tcpshield.provider.ConfigProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit's configuration implementation
 */
public class BukkitConfig extends ConfigProvider {

	public BukkitConfig(JavaPlugin javaPlugin) {
		javaPlugin.saveDefaultConfig();

		FileConfiguration config = javaPlugin.getConfig();

		this.onlyProxy = config.getBoolean("only-allow-proxy-connections");
		this.timestampValidationMode = config.getString("timestamp-validation");
		this.doDebug = config.getBoolean("debug-mode");
	}

}
