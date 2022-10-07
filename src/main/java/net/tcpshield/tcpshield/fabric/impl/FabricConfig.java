package net.tcpshield.tcpshield.fabric.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.tcpshield.tcpshield.fabric.TCPShieldFabric;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.exception.config.ConfigLoadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigReloadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigResetException;
import net.tcpshield.tcpshield.util.exception.phase.ConfigException;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricConfig extends ConfigProvider {

    private final Map<String, String> values = new HashMap<>();

    public FabricConfig() {
        this.dataFolder = new File(FabricLoader.getInstance().getConfigDir().toString());
        this.configFile = new File(dataFolder, "config.yml");

        try {
            reload();
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    @Override
    protected void checkNodes(String... nodes) throws ConfigException {
        for (String node : nodes) {
            if(!values.containsKey(node))
                throw new ConfigException("The node \"" + node + "\" does not exist in the config.");
        }
    }

    @Override
    protected void reset() throws ConfigResetException {
        try {
            values.clear();

            try {
                configFile.delete();
            } catch (Exception ignored) {
                // Just ignore since it either does not exist, or we can overwrite
            }

            // Copy the config.yml data from our mod jar to the loader config folder.
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            }
        } catch (Exception e) {
            throw new ConfigResetException(e);
        }
    }

    @Override
    protected void load() throws ConfigLoadException {
        try {
            values.clear();
            values.putAll(loadConfig());

            checkNodes("only-allow-proxy-connections", "timestamp-validation", "debug-mode", "enable-geyser-support", "prefer-protocollib");

            this.onlyProxy = Boolean.parseBoolean(values.get("only-allow-proxy-connections"));
            this.timestampValidationMode = values.get("timestamp-validation");
            this.doDebug = Boolean.parseBoolean(values.get("debug-mode"));
            this.geyser = Boolean.parseBoolean(values.get("enable-geyser-support"));
            this.preferProtocolLib = Boolean.parseBoolean(values.get("prefer-protocollib"));
        } catch (Exception e) {
            throw new ConfigLoadException(e);
        }
    }

    @Override
    public void reload() throws ConfigReloadException {
        try {
            if(!dataFolder.exists())
                dataFolder.mkdir();

            if(!configFile.exists())
                reset();

            try {
                load();
            } catch (ConfigLoadException exception) {
                TCPShieldFabric.LOGGER.warning("Config loading failed, resetting to default config. (This can be ignored if you just switched builds of TCPShield)");
                reset();
                reload(); // Redo cycle, possible StackOverFlow, but realistically only happens if reset fails
            }
        } catch (Exception e) {
            throw new ConfigReloadException(e);
        }
    }

    private Map<String, String> loadConfig() {
        Map<String, String> configValues = new HashMap<>();

        try {
            List<String> strings = Files.readAllLines(configFile.toPath());

            // Rudimentary config parsing
            for (String line : strings) {
                String[] entry = line.replace(" ", "").split(":");

                // A config option will only be valid if it is in the format 'a: b'.
                // Ensure that is the case now.
                if (entry.length == 2) {
                    String key = entry[0];
                    String value = entry[1];
                    configValues.put(key, value);
                }
            }
        } catch (Exception e) {
            throw new ConfigException("Couldn't load config at config/config.yml!");
        }

        return configValues;
    }
}
