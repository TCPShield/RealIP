package net.tcpshield.tcpshield.bukkit.paper.handler;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.tcpshield.tcpshield.bukkit.provider.BukkitImplProvider;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * The handshake handler for PaperSpigot
 */
public class PaperHandshakeHandler implements Listener {

	private final BukkitImplProvider bukkitProvider;

	public PaperHandshakeHandler(BukkitImplProvider bukkitProvider) {
		this.bukkitProvider = bukkitProvider;
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onHandshake(PlayerHandshakeEvent e) {
		PaperPacket packet = new PaperPacket(e);
		PaperPlayer player = new PaperPlayer(e);

		try {
			bukkitProvider.getPlugin().getPacketHandler().handleHandshake(packet, player);
		} catch (HandshakeException exception) {
			bukkitProvider.getPlugin().getDebugger().exception(exception);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerPing(PaperServerListPingEvent e) {
		if (e.getClient().isLegacy())
			e.setCancelled(true);
	}

}