package net.tcpshield.tcpshield.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.bukkit.impl.BukkitConfigImpl;
import net.tcpshield.tcpshield.geyser.GeyserUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        HandshakePacketHandler handshakePacketHandler = new HandshakePacketHandler(this.getLogger(), new BukkitConfigImpl(this));
        GeyserUtils.initGeyserOrDefault(handshakePacketHandler, () -> {
            BukkitHandshakePacketHandler packetHandler = new BukkitHandshakePacketHandler(this, handshakePacketHandler);
            ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
        });
    }
}
