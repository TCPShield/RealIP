package net.tcpshield.tcpshield.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.PacketAbstraction;
import net.tcpshield.tcpshield.abstraction.PlayerAbstraction;
import net.tcpshield.tcpshield.bukkit.impl.BukkitConfigAbstraction;
import net.tcpshield.tcpshield.bukkit.impl.BukkitPacketAbstraction;
import net.tcpshield.tcpshield.bukkit.impl.BukkitPlayerAbstraction;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitHandshakePacketHandler extends PacketAdapter {

    private final HandshakePacketHandler handshakePacketHandler;

    public BukkitHandshakePacketHandler(JavaPlugin plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Handshake.Client.SET_PROTOCOL);
        this.handshakePacketHandler = new HandshakePacketHandler(plugin.getLogger(), new BukkitConfigAbstraction(plugin));
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        PacketAbstraction packetAbstraction = new BukkitPacketAbstraction(e.getPacket());
        PlayerAbstraction playerAbstraction = new BukkitPlayerAbstraction(e.getPlayer());

        handshakePacketHandler.onHandshake(packetAbstraction, playerAbstraction);
    }
}
