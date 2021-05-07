package net.tcpshield.tcpshield.bukkit.protocollib.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.tcpshield.tcpshield.bukkit.provider.BukkitImplProvider;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;

/**
 * The handshake handler for ProtocolLib
 */
public class ProtocolLibHandshakeHandler extends PacketAdapter {

	private final BukkitImplProvider bukkitProvider;

	public ProtocolLibHandshakeHandler(BukkitImplProvider bukkitProvider) {
		super(bukkitProvider.getPlugin(), ListenerPriority.LOWEST, PacketType.Handshake.Client.SET_PROTOCOL);
		this.bukkitProvider = bukkitProvider;
	}


	@Override
	public void onPacketReceiving(PacketEvent e) {
		ProtocolLibPacket packet = new ProtocolLibPacket(e.getPacket());
		ProtocolLibPlayer player = new ProtocolLibPlayer(e.getPlayer());

		try {
			bukkitProvider.getPlugin().getPacketHandler().handleHandshake(packet, player);
		} catch (HandshakeException exception) {
			bukkitProvider.getPlugin().getDebugger().exception(exception);
		}
	}

}
