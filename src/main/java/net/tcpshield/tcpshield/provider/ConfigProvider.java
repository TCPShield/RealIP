package net.tcpshield.tcpshield.provider;

import net.tcpshield.tcpshield.util.exception.config.ConfigLoadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigReloadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigResetException;
import net.tcpshield.tcpshield.util.exception.phase.ConfigException;

import java.io.File;

/**
 * An abstract provider for TCPShield's options.
 */
public abstract class ConfigProvider {

	/*
	 * Configuration options
	 */
	protected boolean onlyProxy = true;
	protected String timestampValidationMode = "htpdate";
	protected boolean doDebug = true; // Fail-safe default set to true
	protected boolean geyser = false;
	protected boolean velocityHandlePreLoginEvent = true;

	// spigot/paper only option
	protected boolean preferProtocolLib;

	protected File dataFolder;
	protected File configFile;

	public boolean isOnlyProxy() {
		return this.onlyProxy;
	}

	public boolean isGeyser() {
		return this.geyser;
	}

	public boolean handlePreLoginEvent() {
		return this.velocityHandlePreLoginEvent;
	}

	public String getTimestampValidationMode() {
		return this.timestampValidationMode;
	}

	public boolean doDebug() {
		return this.doDebug;
	}

	public boolean preferProtocolLib() {
		return this.preferProtocolLib;
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public File getConfigFile() {
		return configFile;
	}

	/*
	 * Plugin Constants
	 */
	protected final long maxTimestampDifference = 3; // In Unix Timestamps (Seconds)


	public long getMaxTimestampDifference() {
		return maxTimestampDifference;
	}

	/*
	 * Required methods
	 */

	/**
	 * Deletes the current config saved to the disk and installs the default config
	 * @throws ConfigResetException Thrown if resetting fails
	 */
	protected abstract void reset() throws ConfigResetException;

	/**
	 * Trys to load the options from the config, if failed, throws ConfigLoadException
	 * @throws ConfigLoadException Thrown if loading fails, reset should be called if thrown
	 */
	protected abstract void load() throws ConfigLoadException;

	/**
	 * Trys to reload the config, if failed, throws ConfigReloadException
	 * @throws ConfigReloadException Thrown if reloading fails
	 */
	public abstract void reload() throws ConfigReloadException;

	/**
	 * Checks the provided nodes to see if they exist in the config
	 * @param nodes The nodes to check
	 * @throws ConfigException Thrown when a node isn't found
	 */
	protected abstract void checkNodes(String... nodes) throws ConfigException;

}
