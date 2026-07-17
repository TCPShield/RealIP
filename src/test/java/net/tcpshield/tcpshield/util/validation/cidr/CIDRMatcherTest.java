package net.tcpshield.tcpshield.util.validation.cidr;

import net.tcpshield.tcpshield.util.exception.phase.CIDRException;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CIDRMatcherTest {

	@Test
	void matchesIPv4Subnet() throws Exception {
		CIDRMatcher matcher = CIDRMatcher.create("198.51.100.0/24");

		assertTrue(matcher.match(InetAddress.getByName("198.51.100.42")));
		assertFalse(matcher.match(InetAddress.getByName("198.51.101.42")));
	}

	@Test
	void matchesIPv6Subnet() throws Exception {
		CIDRMatcher matcher = CIDRMatcher.create("2001:db8:1234::/48");

		assertTrue(matcher.match(InetAddress.getByName("2001:db8:1234::7")));
		assertFalse(matcher.match(InetAddress.getByName("2001:db8:1235::7")));
	}

	@Test
	void acceptsBareAddressesAsSingleHostRules() throws Exception {
		CIDRMatcher matcher = CIDRMatcher.create("203.0.113.9");

		assertTrue(matcher.match(InetAddress.getByName("203.0.113.9")));
		assertFalse(matcher.match(InetAddress.getByName("203.0.113.10")));
	}

	@Test
	void rejectsInvalidMasks() {
		assertThrows(CIDRException.class, () -> CIDRMatcher.create("192.0.2.1/33"));
		assertThrows(CIDRException.class, () -> CIDRMatcher.create("2001:db8::/129"));
		assertThrows(CIDRException.class, () -> CIDRMatcher.create("192.0.2.1/not-a-mask"));
	}
}
