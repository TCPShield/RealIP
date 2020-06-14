package net.tcpshield.tcpshield.bukkit.impl;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class BukkitConfigImpl extends TCPShieldConfig {

    public BukkitConfigImpl(Plugin plugin) {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        this.onlyProxy = config.getBoolean("only-allow-proxy-connections");
        this.timestampValidationMode = config.getString("timestamp-validation");
        this.debug = config.getBoolean("debug-mode");
    }

}
