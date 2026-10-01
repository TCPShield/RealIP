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

	@Test
	public void ipv6PrefixOf32BitsMatchesTheWholePrefix() throws UnknownHostException {
		assertTrue(matches("2001:db8::/32", "2001:db8::"));
		assertTrue(matches("2001:db8::/32", "2001:db8:ffff:ffff:ffff:ffff:ffff:ffff"));
		assertFalse(matches("2001:db8::/32", "2001:db9::"));
		assertFalse(matches("2001:db8::/32", "2002:db8::"));
	}

	@Test
	public void ipv6PrefixOf128BitsMatchesOnlyItself() throws UnknownHostException {
		assertTrue(matches("2001:db8::1/128", "2001:db8::1"));
		assertFalse(matches("2001:db8::1/128", "2001:db8::2"));
	}

	@Test
	public void ipv4PrefixOf32BitsMatchesOnlyItself() throws UnknownHostException {
		assertTrue(matches("203.0.113.7/32", "203.0.113.7"));
		assertFalse(matches("203.0.113.7/32", "203.0.113.8"));
	}

}
