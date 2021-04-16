package net.tcpshield.tcpshield.bukkit.paper;

import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.bukkit.impl.BukkitConfigImpl;
import net.tcpshield.tcpshield.geyser.GeyserHandshakePacketHandler;
import net.tcpshield.tcpshield.geyser.GeyserUtils;
import net.tcpshield.tcpshield.velocity.VelocityHandshakePacketHandler;
import net.tcpshield.tcpshield.velocity.impl.VelocityConfigImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldPaper {

    private final JavaPlugin plugin;

    public TCPShieldPaper(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        HandshakePacketHandler handshakePacketHandler =  new HandshakePacketHandler(plugin.getLogger(), new BukkitConfigImpl(plugin));

        GeyserUtils.initGeyserOrDefault(handshakePacketHandler, () -> {
            PaperHandshakePacketHandler packetHandler = new PaperHandshakePacketHandler(handshakePacketHandler);
            Bukkit.getPluginManager().registerEvents(packetHandler, plugin);
        });
    }
}
