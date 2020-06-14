package net.tcpshield.tcpshield.bukkit.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.bukkit.impl.BukkitConfigImpl;
import net.tcpshield.tcpshield.bukkit.protocollib.impl.ProtocolLibPacketImpl;
import net.tcpshield.tcpshield.bukkit.protocollib.impl.ProtocolLibPlayerImpl;
import org.bukkit.plugin.Plugin;

public class ProtocolLibHandshakePacketHandler extends PacketAdapter {

    private final HandshakePacketHandler handshakePacketHandler;

    public ProtocolLibHandshakePacketHandler(Plugin plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Handshake.Client.SET_PROTOCOL);
        this.handshakePacketHandler = new HandshakePacketHandler(plugin.getLogger(), new BukkitConfigImpl(plugin));
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        IPacket packet = new ProtocolLibPacketImpl(e.getPacket());
        IPlayer player = new ProtocolLibPlayerImpl(e.getPlayer());

        handshakePacketHandler.onHandshake(packet, player);
    }

}
