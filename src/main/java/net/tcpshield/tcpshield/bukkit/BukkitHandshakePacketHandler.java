package net.tcpshield.tcpshield.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.bukkit.impl.BukkitConfigImpl;
import net.tcpshield.tcpshield.bukkit.impl.BukkitPacketImpl;
import net.tcpshield.tcpshield.bukkit.impl.BukkitPlayerImpl;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitHandshakePacketHandler extends PacketAdapter {

    private final HandshakePacketHandler handshakePacketHandler;

    public BukkitHandshakePacketHandler(JavaPlugin plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Handshake.Client.SET_PROTOCOL);
        this.handshakePacketHandler = new HandshakePacketHandler(plugin.getLogger(), new BukkitConfigImpl(plugin));
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        IPacket packet = new BukkitPacketImpl(e.getPacket());
        IPlayer player = new BukkitPlayerImpl(e.getPlayer());

        handshakePacketHandler.onHandshake(packet, player);
    }
}
