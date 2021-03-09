package net.tcpshield.tcpshield.fabric.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import net.tcpshield.tcpshield.exception.TCPShieldInitializationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FabricConfigImpl extends TCPShieldConfig {

    private final Path configLocation = new File(FabricLoader.getInstance().getConfigDir().toString(), "tcpshield.yml").toPath();

    public FabricConfigImpl() {
        if(!configExists()) {
            createDefaultConfig();
        }

        ConfigData config = loadConfig();

        this.onlyProxy = config.onlyAllowProxyConnections;
        this.ipWhitelistFolder = new File(FabricLoader.getInstance().getGameDirectory(), "ip-whitelist");
        this.geyser = config.enableGeyserCompatibility;
        this.debug = config.debugMode;
    }

    /**
     * @return  whether the file specified in {@link FabricConfigImpl#configLocation} exists.
     */
    private boolean configExists() {
        return Files.exists(configLocation);
    }

    /**
     * Creates the default configuration file at the location specified in {@link FabricConfigImpl#configLocation}.
     *
     * <p>
     * This operation will always override an existing configuration file if it exists.
     * Callers should check {@link FabricConfigImpl#configExists()} if they wish to avoid this behavior.
     */
    private void createDefaultConfig() {
        // Ensure the parent directory of our config file exists.
        // If it does not exist, attempt to create it now.
        Path parentDirectory = configLocation.getParent();
        if (!Files.exists(parentDirectory) || !Files.isDirectory(parentDirectory)) {
            parentDirectory.toFile().mkdirs();
        }

        // Copy the config.yml data from our mod jar to the loader config folder.
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            Files.copy(in, configLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigData loadConfig() {
        try {
            List<String> strings = Files.readAllLines(configLocation);

            boolean onlyAllowProxyConnections = true;
            boolean enableGeyserCompatibility = false;
            boolean debugMode = false;

            // Rudimentary config parsing
            for (String line : strings) {
                String[] keyValue = line.split(": ");

                // A config option will only be valid if it is in the format 'a: b'.
                // Ensure that is the case now.
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];

                    switch (key) {
                        case "only-allow-proxy-connections":
                            onlyAllowProxyConnections = Boolean.parseBoolean(value);
                            break;
                        case "enable-geyser-compatibility":
                            enableGeyserCompatibility = Boolean.parseBoolean(value);
                            break;
                        case "debug-mode":
                            debugMode = Boolean.parseBoolean(value);
                            break;
                    }
                }
            }

            return new ConfigData(onlyAllowProxyConnections, enableGeyserCompatibility, debugMode);
        } catch (Exception e) {
            throw new TCPShieldInitializationException("Couldn't load config in config/tclshield.yml!");
        }
    }

    private static class ConfigData {

        protected boolean onlyAllowProxyConnections;
        protected boolean enableGeyserCompatibility;
        protected boolean debugMode;

        public ConfigData(boolean onlyAllowProxyConnections, boolean enableGeyserCompatibility, boolean debugMode) {
            this.onlyAllowProxyConnections = onlyAllowProxyConnections;
            this.enableGeyserCompatibility = enableGeyserCompatibility;
            this.debugMode = debugMode;
        }
    }
}
