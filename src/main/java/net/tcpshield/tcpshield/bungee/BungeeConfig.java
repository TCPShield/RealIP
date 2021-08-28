package net.tcpshield.tcpshield.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.exception.config.ConfigLoadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigReloadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigResetException;
import net.tcpshield.tcpshield.util.exception.phase.ConfigException;
import net.tcpshield.tcpshield.velocity.VelocityConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Bungee's configuration implementation
 */
public class BungeeConfig extends ConfigProvider {

	private final Plugin plugin;

	private Configuration loadedConfiguration;

	public BungeeConfig(Plugin plugin) throws ConfigException {
		this.plugin = plugin;
		this.dataFolder = plugin.getDataFolder();
		this.configFile = new File(dataFolder, "config.yml");

		try {
			reload();
		} catch (Exception e) {
			throw new ConfigException(e);
		}
	}

	@Override
	protected void checkNodes(String... nodes) {
		for (String node : nodes) {
			if (!loadedConfiguration.contains(node))
				throw new ConfigException("The node \"" + node + "\" does not exist in the config.");
		}
	}

	@Override
	protected void reset() throws ConfigResetException {
		try {
			loadedConfiguration = null; // To the garbage collector you go

			try {
				configFile.delete();
			} catch (Exception ignored) {
				// Just ignore since it either does not exist, or we can overwrite
			}

			try (InputStream in = plugin.getResourceAsStream("config.yml")) {
				Files.copy(in, configFile.toPath());
			}
		} catch (Exception e) {
			throw new ConfigResetException(e);
		}
	}

	@Override
	protected void load() throws ConfigLoadException {
		try {
			ConfigurationProvider configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
			loadedConfiguration = configProvider.load(configFile);

			checkNodes("only-allow-proxy-connections", "timestamp-validation", "debug-mode");

			this.onlyProxy = loadedConfiguration.getBoolean("only-allow-proxy-connections");
			this.timestampValidationMode = loadedConfiguration.getString("timestamp-validation");
			this.doDebug = loadedConfiguration.getBoolean("debug-mode");
			this.geyser = loadedConfiguration.getBoolean("enable-geyser-support");
		} catch (Exception e) {
			throw new ConfigLoadException(e);
		}
	}

	@Override
	public void reload() throws ConfigReloadException {
		try {
			if (!dataFolder.exists())
				dataFolder.mkdir();

			if (!configFile.exists())
				reset();

			try {
				load();
			} catch (ConfigLoadException exception) {
				plugin.getLogger().warning("Config loading failed, resetting to default config. (This can be ignored if you just switched builds of TCPShield)");
				reset();
				reload(); // Redo cycle, possible StackOverFlow, but realistically only happens if reset fails
			}
		} catch (Exception e) {
			throw new ConfigReloadException(e);
		}
	}

}
