package net.tcpshield.tcpshield.velocity.impl;

import com.moandjiezana.toml.Toml;
import net.tcpshield.tcpshield.abstraction.IConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class VelocityConfigImpl implements IConfig {

    private final File dataFolder;
    private final File file;
    private final boolean onlyProxy;
    private final boolean debug;

    public VelocityConfigImpl(File dataFolder) {
        this.dataFolder = dataFolder;
        this.file = new File(this.dataFolder, "config.toml");

        saveDefaultConfig();
        Toml toml = loadConfig();
        this.onlyProxy = toml.getBoolean("only-allow-proxy-connections");
        this.debug = toml.getBoolean("debug-mode");
    }

    @Override
    public boolean isOnlyProxy() {
        return onlyProxy;
    }

    @Override
    public boolean isDebug() {
        return debug;
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
