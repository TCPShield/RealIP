package net.tcpshield.tcpshield.bukkit.protocollib;

import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldProtocolLib {

    private final JavaPlugin plugin;

    public TCPShieldProtocolLib(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        ProtocolLibHandshakePacketHandler packetHandler = new ProtocolLibHandshakePacketHandler(plugin);
        com.comphenix.protocol.ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
    }
}
