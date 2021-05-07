package net.tcpshield.tcpshield.provider;

/**
 * An abstract provider for TCPShield's options.
 */
public abstract class ConfigProvider {

	/*
	 * Configuration options
	 */
	protected boolean onlyProxy;
	protected String timestampValidationMode;
	protected boolean doDebug;


	public boolean isOnlyProxy() {
		return onlyProxy;
	}

	public String getTimestampValidationMode() {
		return this.timestampValidationMode;
	}

	public boolean doDebug() {
		return doDebug;
	}


	/*
	 * Plugin Constants
	 */
	protected final long maxTimestampDifference = 3; // In Unix Timesteps (Seconds)


	public long getMaxTimestampDifference() {
		return maxTimestampDifference;
	}

}
