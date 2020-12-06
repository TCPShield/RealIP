package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import net.tcpshield.tcpshield.impl.TestConfigImpl;
import net.tcpshield.tcpshield.impl.TestPacketImpl;
import net.tcpshield.tcpshield.impl.TestPlayerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HandshakePacketHandlerTest {

    private final TCPShieldConfig defaultTestConfig = new TestConfigImpl();
    private final HandshakePacketHandler standardHandshakePacketHandler = new HandshakePacketHandler(Logger.getLogger("TCPShield"), defaultTestConfig);
    private TestPlayerImpl player;

    @BeforeEach
    public void setUp() {
        player = new TestPlayerImpl(TestConstants.VALID_IP);
    }

    @Test
    public void testSuccessfulHandshake() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.VALID_PAYLOAD, TestConstants.STANDARD_HOSTNAME);

        standardHandshakePacketHandler.onHandshake(packet, player);

        assertEquals(TestConstants.PAYLOAD_HOSTNAME, packet.getHostname());
        assertEquals(TestConstants.PAYLOAD_IP, player.getIP());
    }

    @Test
    public void testSuccessfulFMLTaggedHandshake() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.VALID_PAYLOAD_INVALID_TIMESTAMP_FML_TAGGED, TestConstants.STANDARD_HOSTNAME);

        standardHandshakePacketHandler.onHandshake(packet, player);

        assertEquals(TestConstants.PAYLOAD_FML_HOSTNAME, packet.getHostname());
        assertEquals(TestConstants.PAYLOAD_IP, player.getIP());
    }

    @Test
    public void testUnsuccessfulHandshakeInvalidIP() {
        TestPacketImpl packet = new TestPacketImpl(TestConstants.VALID_PAYLOAD, TestConstants.STANDARD_HOSTNAME);

        TestPlayerImpl player = new TestPlayerImpl(TestConstants.INVALID_IP);
        standardHandshakePacketHandler.onHandshake(packet, player);

        assertFalse(player.isConnected());
    }
}
