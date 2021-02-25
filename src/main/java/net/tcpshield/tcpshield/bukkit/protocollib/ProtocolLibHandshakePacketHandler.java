package net.tcpshield.tcpshield.bukkit.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.bukkit.protocollib.impl.ProtocolLibPacketImpl;
import net.tcpshield.tcpshield.bukkit.protocollib.impl.ProtocolLibPlayerImpl;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtocolLibHandshakePacketHandler extends PacketAdapter {

    private final HandshakePacketHandler handshakePacketHandler;

    public ProtocolLibHandshakePacketHandler(JavaPlugin plugin, HandshakePacketHandler handshakePacketHandler) {
        super(plugin, ListenerPriority.LOWEST, PacketType.Handshake.Client.SET_PROTOCOL);
        this.handshakePacketHandler = handshakePacketHandler;
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        IPacket packet = new ProtocolLibPacketImpl(e.getPacket());
        IPlayer player = new ProtocolLibPlayerImpl(e.getPlayer());

        handshakePacketHandler.onHandshake(packet, player);
    }
}
