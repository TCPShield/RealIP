package net.tcpshield.tcpshield.bukkit.paper;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public class TCPShieldPaper {

    private final Plugin plugin;

    public void load() {
        PaperHandshakePacketHandler packetHandler = new PaperHandshakePacketHandler(plugin);
        Bukkit.getPluginManager().registerEvents(packetHandler, plugin);
    }

}
