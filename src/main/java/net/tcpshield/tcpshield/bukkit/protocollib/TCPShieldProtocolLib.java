package net.tcpshield.tcpshield.bukkit.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public class TCPShieldProtocolLib {

    private final Plugin plugin;

    public void load() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            plugin.getLogger().warning("TCPShield not loading because ProtocolLib is not installed. Either use Paper to enable native compatibility or install ProtocolLib.");
            return;
        }

        ProtocolLibHandshakePacketHandler packetHandler = new ProtocolLibHandshakePacketHandler(plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
    }

}
