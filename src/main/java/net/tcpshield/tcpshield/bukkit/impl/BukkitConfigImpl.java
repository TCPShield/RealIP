package net.tcpshield.tcpshield.bukkit.impl;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BukkitConfigImpl extends TCPShieldConfig {

    public BukkitConfigImpl(JavaPlugin javaPlugin) {
        javaPlugin.saveDefaultConfig();

        FileConfiguration config = javaPlugin.getConfig();

        this.onlyProxy = config.getBoolean("only-allow-proxy-connections");
        this.ipWhitelistFolder = new File(javaPlugin.getDataFolder(), "ip-whitelist");
        this.geyser = config.getBoolean("enable-geyser-compatibility");
        this.debug = config.getBoolean("debug-mode");
    }
}
