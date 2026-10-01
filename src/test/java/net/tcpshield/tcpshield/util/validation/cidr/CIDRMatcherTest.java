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

	@Test
	public void masksLongerThanTheAddressAreRejected() {
		assertThrows(CIDRException.class, () -> CIDRMatcher.create("203.0.113.0/33"));
		assertThrows(CIDRException.class, () -> CIDRMatcher.create("2001:db8::/129"));
	}

	@Test
	public void negativeMasksAreRejected() {
		assertThrows(CIDRException.class, () -> CIDRMatcher.create("203.0.113.0/-1"));
	}

	@Test
	public void ipv4MaskEdges() throws UnknownHostException {
		assertTrue(matches("0.0.0.0/0", "203.0.113.7"));
		assertTrue(matches("128.0.0.0/1", "203.0.113.7"));
		assertFalse(matches("128.0.0.0/1", "127.255.255.255"));
		assertTrue(matches("203.0.0.0/8", "203.255.255.255"));
		assertFalse(matches("203.0.0.0/8", "204.0.0.0"));
		assertTrue(matches("203.0.0.0/9", "203.127.255.255"));
		assertFalse(matches("203.0.0.0/9", "203.128.0.0"));
		assertTrue(matches("203.0.112.0/23", "203.0.113.255"));
		assertFalse(matches("203.0.112.0/23", "203.0.114.0"));
		assertTrue(matches("203.0.113.0/24", "203.0.113.255"));
		assertFalse(matches("203.0.113.0/24", "203.0.112.255"));
		assertTrue(matches("203.0.113.128/25", "203.0.113.255"));
		assertFalse(matches("203.0.113.128/25", "203.0.113.127"));
		assertTrue(matches("203.0.113.6/31", "203.0.113.7"));
		assertFalse(matches("203.0.113.6/31", "203.0.113.8"));
	}

	@Test
	public void ipv6MaskEdges() throws UnknownHostException {
		assertTrue(matches("::/0", "2001:db8::1"));
		assertTrue(matches("8000::/1", "ffff::1"));
		assertFalse(matches("8000::/1", "7fff::1"));
		assertTrue(matches("2001:db8::/48", "2001:db8:0:ffff::1"));
		assertFalse(matches("2001:db8::/48", "2001:db8:1::1"));
		assertTrue(matches("2001:db8::/49", "2001:db8:0:7fff::1"));
		assertFalse(matches("2001:db8::/49", "2001:db8:0:8000::1"));
		assertTrue(matches("2001:db8::/127", "2001:db8::1"));
		assertFalse(matches("2001:db8::/127", "2001:db8::2"));
	}

	@Test
	public void addressFamiliesNeverMatchEachOther() throws UnknownHostException {
		assertFalse(matches("0.0.0.0/0", "2001:db8::1"));
		assertFalse(matches("::/0", "203.0.113.7"));
	}

}
