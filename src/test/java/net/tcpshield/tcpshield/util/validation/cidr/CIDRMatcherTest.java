package net.tcpshield.tcpshield.util.validation.cidr;

import net.tcpshield.tcpshield.util.exception.phase.CIDRException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class CIDRMatcherTest {

	private static boolean matches(String cidr, String address) throws UnknownHostException {
		return CIDRMatcher.create(cidr).match(InetAddress.getByName(address));
	}

	@Test
	public void bareAddressMatchesOnlyItself() throws UnknownHostException {
		assertTrue(matches("203.0.113.7", "203.0.113.7"));
		assertFalse(matches("203.0.113.7", "203.0.113.8"));
	}

	@Test
	public void bareIPv6AddressMatchesOnlyItself() throws UnknownHostException {
		assertTrue(matches("2001:db8::1", "2001:db8::1"));
		assertFalse(matches("2001:db8::1", "2001:db8::2"));
	}

	@Test
	public void emptyEntryIsRejected() {
		assertThrows(CIDRException.class, () -> CIDRMatcher.create(""));
	}

}
