package net.tcpshield.tcpshield.util.validation.cidr;

import net.tcpshield.tcpshield.util.exception.phase.CIDRException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * CIDR matcher for the CIDR validator
 */
public abstract class CIDRMatcher {

	/**
	 * Creates a CIDRMatcher from a CIDR matching regex
	 * @param cidrMatchString The string containing the CIDR matching regex
	 * @return The corrosponding CIDRMatcher
	 */
	public static CIDRMatcher create(String cidrMatchString) {
		return new CIDRMatcher(cidrMatchString) {
			@Override
			public boolean match(InetAddress inetAddress) {
				return super.match(inetAddress);
			}
		};
	}


	private final int maskBits;
	private final int maskBytes;
	private final boolean simpleCIDR;
	private final InetAddress cidrAddress;

	private CIDRMatcher(String cidrMatchString) {
		String[] split = cidrMatchString.split("/", -1);
		if (split.length > 2 || split[0].isBlank()) {
			throw new CIDRException("Invalid CIDR: " + cidrMatchString);
		}
		try {
			cidrAddress = InetAddress.getByName(split[0]);
		} catch (UnknownHostException | SecurityException e) {
			throw new CIDRException(e);
		}

		int addressBits = cidrAddress.getAddress().length * Byte.SIZE;
		try {
			maskBits = split.length == 2 ? Integer.parseInt(split[1]) : addressBits;
		} catch (NumberFormatException e) {
			throw new CIDRException("Invalid CIDR mask: " + cidrMatchString, e);
		}
		if (maskBits < 0 || maskBits > addressBits) {
			throw new CIDRException("CIDR mask is outside 0-" + addressBits + ": " + cidrMatchString);
		}

		simpleCIDR = maskBits == addressBits;
		maskBytes = maskBits / Byte.SIZE;
	}

	/**
	 * Compares the provided InetAddress with the CIDR for a match
	 * @param inetAddress The InetAddress to match with the CIDR
	 * @return Boolean stating if the provided InetAddress matches the CIDR
	 */
	public boolean match(InetAddress inetAddress) {
		if (!cidrAddress.getClass().equals(inetAddress.getClass())) return false; // check if IP is IPv4 or IPv6

		if (simpleCIDR) return inetAddress.equals(cidrAddress); // check for equality if it's a simple CIDR

		byte[] inetAddressBytes = inetAddress.getAddress();
		byte[] requiredAddressBytes = cidrAddress.getAddress();

		byte finalByte = (byte) (0xFF00 >> (maskBits & 0x07));

		for (int i = 0; i < maskBytes; i++) {
			if (inetAddressBytes[i] != requiredAddressBytes[i]) return false;
		}

		if (finalByte != 0)
			return (inetAddressBytes[maskBytes] & finalByte) == (requiredAddressBytes[maskBytes] & finalByte);

		return true;
	}

}
