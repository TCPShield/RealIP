package net.tcpshield.tcpshield.bukkit.protocollib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldProtocolLib {

    private final JavaPlugin plugin;

    public TCPShieldProtocolLib(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            plugin.getLogger().warning("TCPShield not loading because ProtocolLib is not installed. Either use Paper to enable native compatibility or install ProtocolLib.");
            return;
        }

        ProtocolLibHandshakePacketHandler packetHandler = new ProtocolLibHandshakePacketHandler(plugin);
        com.comphenix.protocol.ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
    }
}
