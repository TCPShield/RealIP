package net.tcpshield.tcpshield.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        BukkitHandshakePacketHandler packetHandler = new BukkitHandshakePacketHandler(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(packetHandler);
    }
}
