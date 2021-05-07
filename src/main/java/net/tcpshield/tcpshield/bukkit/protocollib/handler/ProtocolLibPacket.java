package net.tcpshield.tcpshield.bukkit.protocollib.handler;

import com.comphenix.protocol.events.PacketContainer;
import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.util.exception.manipulate.PacketManipulationException;

/**
 * A packet wrapper for ProtocolLib
 */
public class ProtocolLibPacket implements PacketProvider {

	private final PacketContainer packetContainer;
	private final String rawPayload;

	public ProtocolLibPacket(PacketContainer packetContainer) {
		this.packetContainer = packetContainer;
		this.rawPayload = packetContainer.getStrings().read(0);
	}


	@Override
	public String getPayloadString() {
		return rawPayload;
	}

	@Override
	public void setPacketHostname(String hostname) throws PacketManipulationException {
		try {
			packetContainer.getStrings().write(0, hostname);
		} catch (Exception e) {
			throw new PacketManipulationException(e);
		}
	}

}
