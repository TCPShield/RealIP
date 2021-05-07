package net.tcpshield.tcpshield.bukkit.protocollib.handler;

import com.comphenix.protocol.injector.server.SocketInjector;
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.ReflectionUtil;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import net.tcpshield.tcpshield.util.exception.phase.ReflectionException;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * A player wrapper for ProtocolLib
 */
public class ProtocolLibPlayer implements PlayerProvider {

	/*
	 * Reflection objects needed for manipulation
	 */
	private static Class<?> abstractChannelClass;

	static {
		try {
			abstractChannelClass = Class.forName("io.netty.channel.AbstractChannel");
		} catch (ClassNotFoundException e) {
			try {
				abstractChannelClass = Class.forName("net.minecraft.util.io.netty.channel.AbstractChannel");
			} catch (ClassNotFoundException e2) {
				throw new InitializationException(new ReflectionException(e2));
			}
		}
	}


	private final Player player;
	private String ip;

	public ProtocolLibPlayer(Player player) {
		this.player = player;
		this.ip = player.getAddress().getAddress().getHostAddress();
	}


	/**
	 * Unsupported with ProtocolLib handshakes
	 * @return unknown
	 */
	@Override
	public String getUUID() {
		return "unknown";
	}

	/**
	 * Unsupported with ProtocolLib handshakes
	 * @return unknown
	 */
	@Override
	public String getName() {
		return "unknown";
	}

	@Override
	public String getIP() {
		return ip;
	}

	@Override
	public void setIP(InetSocketAddress ip) throws PlayerManipulationException {
		try {
			this.ip = ip.getAddress().getHostAddress();

			SocketInjector ignored = TemporaryPlayerFactory.getInjectorFromPlayer(player);
			Object injector = ReflectionUtil.getObjectInPrivateField(ignored, "injector");
			Object networkManager = ReflectionUtil.getObjectInPrivateField(injector, "networkManager");

			ReflectionUtil.setFinalField(networkManager, ReflectionUtil.searchFieldByClass(networkManager.getClass(), SocketAddress.class), ip);

			Object channel = ReflectionUtil.getObjectInPrivateField(injector, "originalChannel");
			ReflectionUtil.setFinalField(channel, ReflectionUtil.getDeclaredField(abstractChannelClass, "remoteAddress"), ip);
		} catch (Exception e) {
			throw new PlayerManipulationException(e);
		}
	}

	@Override
	public void disconnect() {
		player.kickPlayer("");
	}

}
