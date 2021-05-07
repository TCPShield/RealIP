package net.tcpshield.tcpshield.provider;

import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;

import java.net.InetSocketAddress;

/**
 * A player provider for TCPShield handling
 */
public interface PlayerProvider {

	String getUUID();

	String getName();

	String getIP();

	/**
	 * Sets the players IP address directly to the server
	 *
	 * @param ip The new IP address to pass to the proxy/server
	 */
	void setIP(InetSocketAddress ip) throws PlayerManipulationException;

	void disconnect();

}
