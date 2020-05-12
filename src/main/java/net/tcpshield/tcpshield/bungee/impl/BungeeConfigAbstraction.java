package net.tcpshield.tcpshield.bungee.impl;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.tcpshield.tcpshield.abstraction.ConfigAbstraction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeeConfigAbstraction implements ConfigAbstraction {

    private final Plugin plugin;
    private final boolean onlyProxy;
    private final boolean debug;

    public BungeeConfigAbstraction(Plugin plugin) {
        this.plugin = plugin;

        Configuration config = loadConfig();
        this.onlyProxy = config.getBoolean("only-allow-proxy-connections");
        this.debug = config.getBoolean("debug-mode");
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
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        File file = getConfigFile();
        if (file.exists()) return;

        try (InputStream in = plugin.getResourceAsStream("config.yml")) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getConfigFile() {
        File dataFolder = plugin.getDataFolder();
        return new File(dataFolder, "config.yml");
    }

    private Configuration loadConfig() {
        try {
            ConfigurationProvider configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            File configFile = getConfigFile();
            return configProvider.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load config", e);
        }
    }
}
