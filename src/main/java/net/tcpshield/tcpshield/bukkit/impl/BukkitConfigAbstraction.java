package net.tcpshield.tcpshield.bukkit.impl;

import net.tcpshield.tcpshield.abstraction.ConfigAbstraction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitConfigAbstraction implements ConfigAbstraction {

    private final boolean onlyProxy;
    private final boolean debug;

    public BukkitConfigAbstraction(JavaPlugin javaPlugin) {
        javaPlugin.saveDefaultConfig();

        FileConfiguration config = javaPlugin.getConfig();
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
}
