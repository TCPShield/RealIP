package net.tcpshield.tcpshield.bukkit.paper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldPaper {

    private final JavaPlugin plugin;

    public TCPShieldPaper(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        PaperHandshakePacketHandler packetHandler = new PaperHandshakePacketHandler(plugin);
        Bukkit.getPluginManager().registerEvents(packetHandler, plugin);
    }
}
