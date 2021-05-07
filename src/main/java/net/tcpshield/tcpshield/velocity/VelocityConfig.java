package net.tcpshield.tcpshield.velocity;

import com.moandjiezana.toml.Toml;
import net.tcpshield.tcpshield.provider.ConfigProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Velocity's configuration implementation
 */
public class VelocityConfig extends ConfigProvider {

	private final File dataFolder;
	private final File file;

	public VelocityConfig(File dataFolder) throws IOException {
		this.dataFolder = dataFolder;
		this.file = new File(this.dataFolder, "config.toml");

		saveDefaultConfig();
		Toml toml = loadConfig();

		this.onlyProxy = toml.getBoolean("only-allow-proxy-connections");
		this.timestampValidationMode = toml.getString("timestamp-validation");
		this.doDebug = toml.getBoolean("debug-mode");
	}


	private void saveDefaultConfig() throws IOException {
		if (!dataFolder.exists()) dataFolder.mkdir();
		if (file.exists()) return;

		try (InputStream in = VelocityConfig.class.getResourceAsStream("/config.toml")) {
			Files.copy(in, file.toPath());
		} catch (IOException e) {
			throw e;
		}
	}

	private File getConfigFile() {
		return new File(dataFolder, "config.toml");
	}

	private Toml loadConfig() {
		return new Toml().read(getConfigFile());
	}

}
