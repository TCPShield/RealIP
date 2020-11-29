package net.tcpshield.tcpshield.bukkit.paper;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.bukkit.impl.BukkitConfigImpl;
import net.tcpshield.tcpshield.bukkit.paper.impl.PaperPacketImpl;
import net.tcpshield.tcpshield.bukkit.paper.impl.PaperPlayerImpl;
import net.tcpshield.tcpshield.bukkit.paper.impl.PaperServerListPacketImpl;
import net.tcpshield.tcpshield.bukkit.paper.impl.PaperServerListPingPlayerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperHandshakePacketHandler implements Listener {

    private final HandshakePacketHandler handshakePacketHandler;

    public PaperHandshakePacketHandler(JavaPlugin plugin) {
        this.handshakePacketHandler = new HandshakePacketHandler(plugin.getLogger(), new BukkitConfigImpl(plugin));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHandshake(PlayerHandshakeEvent e) {
        IPacket packet = new PaperPacketImpl(e);
        IPlayer player = new PaperPlayerImpl(e);

        handshakePacketHandler.onHandshake(packet, player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerPing(PaperServerListPingEvent e) {
        if (e.getClient().isLegacy()) e.setCancelled(true);

        IPacket packet = new PaperServerListPacketImpl(e);
        IPlayer player = new PaperServerListPingPlayerImpl(e);

        handshakePacketHandler.onHandshake(packet, player);
    }
}
