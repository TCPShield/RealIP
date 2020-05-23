package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.abstraction.IConfig;
import net.tcpshield.tcpshield.impl.TestConfigImpl;
import net.tcpshield.tcpshield.impl.TestPacketImpl;
import net.tcpshield.tcpshield.impl.TestPlayerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HandshakePacketHandlerTest {

    private final IConfig standardTestConfig = new TestConfigImpl(true);
    private final IConfig skippingTimestampCheckTestConfig = new TestConfigImpl(false);
    private final HandshakePacketHandler standardHandshakePacketHandler = new HandshakePacketHandler(Logger.getLogger("TCPShield"), standardTestConfig);
    private final HandshakePacketHandler skippingTimestampCheckHandshakePacketHandler = new HandshakePacketHandler(Logger.getLogger("TCPShield"), skippingTimestampCheckTestConfig);
    private TestPlayerImpl player;

    @BeforeEach
    public void setUp() {
        player = new TestPlayerImpl();
    }

    @Test
    public void testSuccessfulHandshake() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.VALID_PAYLOAD_INVALID_TIMESTAMP, TestConstants.STANDARD_HOSTNAME);

        skippingTimestampCheckHandshakePacketHandler.onHandshake(packet, player);

        assertEquals(TestConstants.PAYLOAD_HOSTNAME, packet.getHostname());
        assertEquals(TestConstants.PAYLOAD_IP, player.getIP());
    }

    @Test
    public void testSuccessfulFMLTaggedHandshake() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.VALID_PAYLOAD_INVALID_TIMESTAMP_FML_TAGGED, TestConstants.STANDARD_HOSTNAME);

        skippingTimestampCheckHandshakePacketHandler.onHandshake(packet, player);

        assertEquals(TestConstants.PAYLOAD_FML_HOSTNAME, packet.getHostname());
        assertEquals(TestConstants.PAYLOAD_IP, player.getIP());
    }

    @Test
    public void testUnsuccessfulHandshakeFailedTimestampCheck() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.VALID_PAYLOAD_INVALID_TIMESTAMP, TestConstants.STANDARD_HOSTNAME);

        standardHandshakePacketHandler.onHandshake(packet, player);

        assertFalse(player.isConnected());
    }

    @Test
    public void testUnsuccessfulHandshakeMalformedPayload() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.INVALID_PAYLOAD_MALFORMED_PAYLOAD, TestConstants.STANDARD_HOSTNAME);

        skippingTimestampCheckHandshakePacketHandler.onHandshake(packet, player);

        assertFalse(player.isConnected());
    }

    @Test
    public void testUnsuccessfulHandshakeInvalidSignature() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.INVALID_PAYLOAD_INVALID_SIGNATURE, TestConstants.STANDARD_HOSTNAME);

        skippingTimestampCheckHandshakePacketHandler.onHandshake(packet, player);

        assertFalse(player.isConnected());
    }
}
