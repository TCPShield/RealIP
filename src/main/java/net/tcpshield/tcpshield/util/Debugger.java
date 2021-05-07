package net.tcpshield.tcpshield.util;

import net.tcpshield.tcpshield.TCPShieldPlugin;

import java.util.logging.Logger;

/**
 * A util for debugging
 */
public abstract class Debugger {

	/**
	 * Creates a debugger instance according to
	 * the plugin's configuration options
	 * @param plugin The TCPShield plugin
	 * @return A debugger instance from plugin's configuration
	 */
	public static Debugger createDebugger(TCPShieldPlugin plugin) {
		if (plugin.getConfigProvider().doDebug())
			/*
			 * A non-empty debugger using the plugin's logger
			 */
			return new Debugger(plugin.getLogger()) {

				@Override
				public void info(String format, Object... formats) {
					this.logger.info("Debug : " + String.format(format, formats));
				}

				@Override
				public void warn(String format, Object... formats) {
					this.logger.warning("Debug : " + String.format(format, formats));
				}

				@Override
				public void error(String format, Object... formats) {
					this.logger.severe("Debug : " + String.format(format, formats));
				}

				@Override
				public void exception(Exception exception) {
					exception.printStackTrace();
				}

			};
		else
			/*
			 * An empty debugger
			 */
			return new Debugger(null) {

				@Override
				public void info(String format, Object... formats) {

				}

				@Override
				public void warn(String format, Object... formats) {

				}

				@Override
				public void error(String format, Object... formats) {

				}

				@Override
				public void exception(Exception exception) {

				}

			};
	}


	protected final Logger logger;

	/**
	 * Non-accessable constructor for creating a debugger
	 * @param logger The plugin's logger
	 */
	private Debugger(Logger logger) {
		this.logger = logger;
	}


	/**
	 * Outputs debug information with log level "INFO"
	 * @param format The output string to be formatted
	 * @param formats The formarts for the string
	 */
	public abstract void info(String format, Object... formats);

	/**
	 * Outputs debug information with log level "WARNING"
	 * @param format The output string to be formatted
	 * @param formats The formarts for the string
	 */
	public abstract void warn(String format, Object... formats);

	/**
	 * Outputs debug information with log level "SEVERE"
	 * @param format The output string to be formatted
	 * @param formats The formarts for the string
	 */
	public abstract void error(String format, Object... formats);

	/**
	 * Outputs the exception through the debugger
	 * @param exception The exception to be outputted
	 */
	public abstract void exception(Exception exception);

}
