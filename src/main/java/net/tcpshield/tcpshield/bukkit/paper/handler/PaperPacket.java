package net.tcpshield.tcpshield.bukkit.paper.handler;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.util.exception.manipulate.PacketManipulationException;

/**
 * A packet wrapper for PaperSpigot
 */
public class PaperPacket implements PacketProvider {

	private final PlayerHandshakeEvent handshakeEvent;

	public PaperPacket(PlayerHandshakeEvent handshakeEvent) {
		this.handshakeEvent = handshakeEvent;
	}


	@Override
	public String getPayloadString() {
		return handshakeEvent.getOriginalHandshake();
	}

	@Override
	public void setPacketHostname(String hostname) throws PacketManipulationException {
		try {
			handshakeEvent.setCancelled(false);
			handshakeEvent.setServerHostname(hostname);
		} catch (Exception e) {
			throw new PacketManipulationException(e);
		}
	}

}
