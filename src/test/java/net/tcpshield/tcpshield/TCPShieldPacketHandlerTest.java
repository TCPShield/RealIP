package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.geyser.GeyserUtils;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.Debugger;
import net.tcpshield.tcpshield.util.exception.manipulate.PacketManipulationException;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;
import net.tcpshield.tcpshield.util.exception.parse.InvalidPayloadException;
import net.tcpshield.tcpshield.util.exception.parse.TimestampValidationException;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;
import net.tcpshield.tcpshield.util.validation.SignatureValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class TCPShieldPacketHandlerTest {

	private static final String[] MALFORMED_PAYLOADS = {
			"",
			"example.com",
			"example.com///1.2.3.4:25565",
			"example.com///1.2.3.4:25565///1700000000",
			"example.com///1.2.3.4:25565///1700000000///signature///extra",
			"///////",
			"///:///0///",
			"\0",
			"example.com///1.2.3.4///1700000000///signature",
			"example.com///1.2.3.4:port///1700000000///signature",
			"example.com///1.2.3.4:25565///timestamp///signature",
			"example.com///1.2.3.4:25565///99999999999999999999///signature",
			"example.com///1.2.3.4:25565//////signature",
			"example.com///:///1700000000///signature",
			"example.com///1.2.3.4:25565///1700000000///" + "A".repeat(100000),
			"x".repeat(1000000)
	};

	private static final String CLIENT_IP = "198.51.100.9";

	@TempDir
	Path dataFolder;

	private boolean onlyProxy;
	private TCPShieldPacketHandler handler;

	@BeforeEach
	public void setUp() throws Exception {
		onlyProxy = true;
		handler = createHandler();
	}

	@AfterEach
	public void tearDown() {
		GeyserUtils.GEYSER_SUPPORT_ENABLED = false;
	}

	private TCPShieldPacketHandler createHandler() throws Exception {
		ConfigProvider configProvider = new ConfigProvider() {

			{
				this.dataFolder = TCPShieldPacketHandlerTest.this.dataFolder.toFile();
				this.timestampValidationMode = "system";
				this.doDebug = false;
				this.onlyProxy = TCPShieldPacketHandlerTest.this.onlyProxy;
			}

			@Override
			protected void reset() {
			}

			@Override
			protected void load() {
			}

			@Override
			public void reload() {
			}

			@Override
			protected void checkNodes(String... nodes) {
			}

		};

		TCPShieldPlugin plugin = new TCPShieldPlugin() {

			private Debugger debugger;

			@Override
			public ConfigProvider getConfigProvider() {
				return configProvider;
			}

			@Override
			public Logger getLogger() {
				return Logger.getAnonymousLogger();
			}

			@Override
			public TCPShieldPacketHandler getPacketHandler() {
				return null;
			}

			@Override
			public Debugger getDebugger() {
				if (debugger == null)
					debugger = Debugger.createDebugger(this);

				return debugger;
			}

		};

		return new TCPShieldPacketHandler(plugin);
	}

	private static class TestPacket implements PacketProvider {

		private final String payload;
		private String hostname;

		private TestPacket(String payload) {
			this.payload = payload;
		}

		@Override
		public String getPayloadString() {
			return payload;
		}

		@Override
		public void setPacketHostname(String hostname) throws PacketManipulationException {
			this.hostname = hostname;
		}

	}

	private static class TestPlayer implements PlayerProvider {

		private final String ip;
		private InetSocketAddress newIP;
		private boolean disconnected;

		private TestPlayer(String ip) {
			this.ip = ip;
		}

		@Override
		public String getUUID() {
			return "unknown";
		}

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
			this.newIP = ip;
		}

		@Override
		public void disconnect() {
			disconnected = true;
		}

	}

	private void whitelist(String... lines) throws Exception {
		File folder = new File(dataFolder.toFile(), "ip-whitelist");
		folder.mkdir();
		Files.write(new File(folder, "test.txt").toPath(), String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void malformedPayloadsAreRejected() {
		for (String payload : MALFORMED_PAYLOADS) {
			TestPacket packet = new TestPacket(payload);
			TestPlayer player = new TestPlayer(CLIENT_IP);

			assertThrows(HandshakeException.class, () -> handler.handleHandshake(packet, player), "payload: " + payload.substring(0, Math.min(payload.length(), 60)));

			assertTrue(player.disconnected);
			assertNull(player.newIP);
			assertNull(packet.hostname);
		}
	}

	@Test
	public void payloadWithoutProxyInfoIsRejected() {
		TestPlayer player = new TestPlayer(CLIENT_IP);

		HandshakeException exception = assertThrows(HandshakeException.class, () -> handler.handleHandshake(new TestPacket("example.com"), player));

		assertTrue(exception.getCause() instanceof InvalidPayloadException);
		assertTrue(player.disconnected);
	}

	@Test
	public void payloadWithoutProxyInfoIsNotDisconnectedWhenProxyOnlyIsOff() throws Exception {
		onlyProxy = false;
		handler = createHandler();
		TestPlayer player = new TestPlayer(CLIENT_IP);

		assertThrows(HandshakeException.class, () -> handler.handleHandshake(new TestPacket("example.com"), player));

		assertFalse(player.disconnected);
		assertNull(player.newIP);
	}

	@Test
	public void invalidSignatureIsRejected() {
		long now = System.currentTimeMillis() / 1000;
		TestPacket packet = new TestPacket("example.com///203.0.113.7:25565///" + now + "///MGUCMQDh1lCBfLdMxcMqwI/WKbFSo9w8N0KP+KflwRWxY8+fXJKqNL2546X5zFyEldhdTasCMD4XQjviTLdzPOAlkxCQKXAP4SeSZWhLEVtbMQI514tbj27YLnSKTRyTeWi7GT9Log==");
		TestPlayer player = new TestPlayer(CLIENT_IP);

		assertThrows(HandshakeException.class, () -> handler.handleHandshake(packet, player));

		assertTrue(player.disconnected);
		assertNull(player.newIP);
		assertNull(packet.hostname);
	}

	@Test
	public void staleTimestampIsRejectedBeforeTheSignatureIsChecked() {
		TestPacket packet = new TestPacket("example.com///203.0.113.7:25565///1700000000///signature");
		TestPlayer player = new TestPlayer(CLIENT_IP);

		HandshakeException exception = assertThrows(HandshakeException.class, () -> handler.handleHandshake(packet, player));

		assertTrue(exception.getCause() instanceof TimestampValidationException);
		assertTrue(player.disconnected);
	}

	@Test
	public void timestampBeyondTheIntegerRangeIsAValidTimestamp() {
		TestPacket packet = new TestPacket("example.com///203.0.113.7:25565///3000000000///signature");
		TestPlayer player = new TestPlayer(CLIENT_IP);

		HandshakeException exception = assertThrows(HandshakeException.class, () -> handler.handleHandshake(packet, player));

		assertTrue(exception.getCause() instanceof TimestampValidationException);
	}

	@Test
	public void whitelistedAddressMayConnectWithoutProxyInfo() throws Exception {
		whitelist("198.51.100.0/24");
		handler = createHandler();
		TestPlayer player = new TestPlayer(CLIENT_IP);

		assertDoesNotThrow(() -> handler.handleHandshake(new TestPacket("example.com"), player));

		assertFalse(player.disconnected);
		assertNull(player.newIP);
	}

	@Test
	public void whitelistedBareAddressMayConnectWithoutProxyInfo() throws Exception {
		whitelist("", "198.51.100.9", "");
		handler = createHandler();

		TestPlayer whitelisted = new TestPlayer(CLIENT_IP);
		assertDoesNotThrow(() -> handler.handleHandshake(new TestPacket("example.com"), whitelisted));
		assertFalse(whitelisted.disconnected);

		TestPlayer other = new TestPlayer("198.51.100.10");
		assertThrows(HandshakeException.class, () -> handler.handleHandshake(new TestPacket("example.com"), other));
		assertTrue(other.disconnected);

		TestPlayer loopback = new TestPlayer("127.0.0.1");
		assertThrows(HandshakeException.class, () -> handler.handleHandshake(new TestPacket("example.com"), loopback));
		assertTrue(loopback.disconnected);
	}

	@Test
	public void unresolvableAddressIsRejected() {
		TestPlayer player = new TestPlayer("not an address.invalid");

		assertThrows(HandshakeException.class, () -> handler.handleHandshake(new TestPacket("example.com"), player));

		assertTrue(player.disconnected);
	}

	/**
	 * Replaces the signature validator with one that accepts everything and records what it was asked to validate
	 */
	private List<String> acceptAnySignature() throws Exception {
		List<String> validated = new ArrayList<>();

		Field field = TCPShieldPacketHandler.class.getDeclaredField("signatureValidator");
		field.setAccessible(true);
		field.set(handler, new SignatureValidator() {

			@Override
			public boolean validate(String str, String signature) {
				validated.add(str);
				return true;
			}

		});

		return validated;
	}

	private void assertClientAddress(String ipData, String expectedHost, int expectedPort) throws Exception {
		List<String> validated = acceptAnySignature();
		long now = System.currentTimeMillis() / 1000;
		TestPacket packet = new TestPacket("example.com///" + ipData + "///" + now + "///signature");
		TestPlayer player = new TestPlayer(CLIENT_IP);

		handler.handleHandshake(packet, player);

		assertEquals(new InetSocketAddress(InetAddress.getByName(expectedHost), expectedPort), player.newIP);
		assertEquals("example.com", packet.hostname);
		assertEquals(List.of("example.com///" + ipData + "///" + now), validated);
		assertFalse(player.disconnected);
	}

	@Test
	public void ipv4ClientAddressIsApplied() throws Exception {
		assertClientAddress("203.0.113.7:25565", "203.0.113.7", 25565);
	}

	@Test
	public void rawIPv6ClientAddressIsApplied() throws Exception {
		assertClientAddress("2001:db8::1:25565", "2001:db8::1", 25565);
		assertClientAddress("2001:db8:0:0:0:0:0:1:1234", "2001:db8::1", 1234);
		assertClientAddress("::1:25565", "::1", 25565);
	}

	@Test
	public void bracketedIPv6ClientAddressIsApplied() throws Exception {
		assertClientAddress("[2001:db8::1]:25565", "2001:db8::1", 25565);
		assertClientAddress("[::1]:1", "::1", 1);
	}

	@Test
	public void ipv4MappedIPv6ClientAddressIsApplied() throws Exception {
		assertClientAddress("::ffff:203.0.113.7:25565", "203.0.113.7", 25565);
		assertClientAddress("[::ffff:203.0.113.7]:25565", "203.0.113.7", 25565);
		assertTrue(TCPShieldPacketHandler.parseClientAddress("::ffff:203.0.113.7:25565").getAddress() instanceof Inet4Address);
	}

	@Test
	public void ipv6ClientAddressOverGeyserIsApplied() throws Exception {
		GeyserUtils.GEYSER_SUPPORT_ENABLED = true;
		TestPacket packet = new TestPacket("2001:db8::1:0///" + GeyserUtils.SESSION_SECRET + "///0///example.com");
		TestPlayer player = new TestPlayer(CLIENT_IP);

		handler.handleHandshake(packet, player);

		assertEquals(new InetSocketAddress(InetAddress.getByName("2001:db8::1"), 0), player.newIP);
		assertEquals("example.com", packet.hostname);
	}

	@Test
	public void malformedClientAddressesAreRejected() throws Exception {
		acceptAnySignature();
		String[] malformed = {
				"1.2.3.4",
				"2001:db8::1",
				"[2001:db8::1]",
				"1.2.3.4:",
				":25565",
				"[]:25565",
				"[:25565",
				"]:25565",
				"[2001:db8::1:25565",
				"2001:db8::1]:25565",
				"1.2.3.4:port",
				"[2001:db8::1]:port",
				"1.2.3.4:99999",
				"1.2.3.4:-1",
				":"
		};

		for (String ipData : malformed) {
			long now = System.currentTimeMillis() / 1000;
			TestPacket packet = new TestPacket("example.com///" + ipData + "///" + now + "///signature");
			TestPlayer player = new TestPlayer(CLIENT_IP);

			assertThrows(HandshakeException.class, () -> handler.handleHandshake(packet, player), ipData);

			assertTrue(player.disconnected, ipData);
			assertNull(player.newIP, ipData);
			assertNull(packet.hostname, ipData);
		}
	}

}
