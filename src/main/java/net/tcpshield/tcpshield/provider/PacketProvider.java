package net.tcpshield.tcpshield.provider;

import net.tcpshield.tcpshield.util.exception.manipulate.PacketManipulationException;

/**
 * A packet provider for TCPShield handling
 */
public interface PacketProvider {

	/**
	 * Gets the raw payload from the packet
	 * @return The raw payload in the form of a String
	 */
	String getPayloadString();

	/**
	 * Sets the hostname within the packet
	 * @param hostname The new hostname
	 */
	void setPacketHostname(String hostname) throws PacketManipulationException;

}
