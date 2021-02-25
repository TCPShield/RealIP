package net.tcpshield.tcpshield.bukkit.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.bukkit.impl.BukkitConfigImpl;
import net.tcpshield.tcpshield.geyser.GeyserUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldProtocolLib {

    private final JavaPlugin plugin;

    public TCPShieldProtocolLib(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        HandshakePacketHandler handshakePacketHandler = new HandshakePacketHandler(plugin.getLogger(), new BukkitConfigImpl(plugin));
        GeyserUtils.initGeyserOrDefault(handshakePacketHandler, () -> {
            ProtocolLibHandshakePacketHandler packetHandler = new ProtocolLibHandshakePacketHandler(plugin, handshakePacketHandler);
            ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
        });
    }
}
