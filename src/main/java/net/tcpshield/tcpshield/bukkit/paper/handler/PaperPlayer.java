package net.tcpshield.tcpshield.bukkit.paper.handler;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * A player wrapper for PaperSpigot
 */
public class PaperPlayer implements PlayerProvider {

	private final PlayerHandshakeEvent handshakeEvent;

	public PaperPlayer(PlayerHandshakeEvent handshakeEvent) {
		this.handshakeEvent = handshakeEvent;
	}


	/**
	 * Trys to grab the UUID of the handshake
	 * @return If found, the corrosponding uuid, if not, unknown
	 */
	@Override
	public String getUUID() {
		UUID uuid = handshakeEvent.getUniqueId();
		if (uuid == null)
			return "unknown";

		return uuid.toString();
	}

	/**
	 * Unsupported with Paper handshakes
	 * @return unknown
	 */
	@Override
	public String getName() {
		return "unknown";
	}

	@Override
	public String getIP() {
		return handshakeEvent.getSocketAddressHostname();
	}

	@Override
	public void setIP(InetSocketAddress ip) throws PlayerManipulationException {
		try {
			handshakeEvent.setSocketAddressHostname(ip.getAddress().getHostAddress());
		} catch (Exception e) {
			throw new PlayerManipulationException(e);
		}
	}

	@Override
	public void disconnect() {
		handshakeEvent.setCancelled(false); // Caused issues with newer versions of Paper (Thanks https://github.com/realDragonium)
		handshakeEvent.setFailMessage("Connection failed. Please try again or contract an administrator.");
		handshakeEvent.setFailed(true);
	}

}
