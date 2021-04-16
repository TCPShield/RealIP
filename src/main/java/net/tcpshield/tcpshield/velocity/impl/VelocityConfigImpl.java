package net.tcpshield.tcpshield.velocity.impl;

import com.moandjiezana.toml.Toml;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class VelocityConfigImpl extends TCPShieldConfig {

    private final File dataFolder;
    private final File file;

    public VelocityConfigImpl(File dataFolder) {
        this.dataFolder = dataFolder;
        this.file = new File(this.dataFolder, "config.toml");

        saveDefaultConfig();
        Toml toml = loadConfig();
        this.onlyProxy = toml.getBoolean("only-allow-proxy-connections");
        this.ipWhitelistFolder = new File(dataFolder, "ip-whitelist");
        this.geyser = toml.getBoolean("enable-geyser-compatibility");
        this.debug = toml.getBoolean("debug-mode");
    }

    private void saveDefaultConfig() {
        if (!dataFolder.exists()) dataFolder.mkdir();
        if (file.exists()) return;

        try (InputStream in = VelocityConfigImpl.class.getResourceAsStream("/config.toml")) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getConfigFile() {
        return new File(dataFolder, "config.toml");
    }

    private Toml loadConfig() {
        return new Toml().read(getConfigFile());
    }
}
