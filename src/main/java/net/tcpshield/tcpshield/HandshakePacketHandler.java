package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.abstraction.ConfigAbstraction;
import net.tcpshield.tcpshield.abstraction.PacketAbstraction;
import net.tcpshield.tcpshield.abstraction.PlayerAbstraction;
import net.tcpshield.tcpshield.exception.*;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class HandshakePacketHandler {

    private final Logger logger;
    private final SignatureVerifier signatureVerifier;
    private final ConfigAbstraction configAbstraction;

    public HandshakePacketHandler(Logger logger, ConfigAbstraction configAbstraction) {
        try {
            this.logger = logger;
            this.signatureVerifier = new SignatureVerifier();
            this.configAbstraction = configAbstraction;
        } catch (Exception e) {
            throw new TCPShieldInitializationException(e);
        }
    }

    public void onHandshake(PacketAbstraction packetAbstraction, PlayerAbstraction playerAbstraction) {
        String rawPayload = packetAbstraction.getRawPayload();
        try {
            String extraData = null;

            // fix for e.g. incoming FML tagged packets
            String cleanedPayload;
            int nullIndex = rawPayload.indexOf('\0');
            if (nullIndex != -1) { // FML tagged
                cleanedPayload = rawPayload.substring(0, nullIndex);
                extraData = rawPayload.substring(nullIndex);
            } else { // standard
                cleanedPayload = rawPayload;
            }

            String[] payload = cleanedPayload.split("///", 4);
            if (payload.length != 4)
                throw new MalformedPayloadException("payload.length != 4. Raw payload = \"" + rawPayload + "\"");

            String hostname = payload[0];
            String ipData = payload[1];
            int timestamp;
            try {
                timestamp = Integer.parseInt(payload[2]);
            } catch (NumberFormatException e) {
                throw new MalformedPayloadException(e);
            }
            String signature = payload[3];

            long currentTime = System.currentTimeMillis() / 1000;
            if (!(timestamp >= (currentTime - 2) && timestamp <= (currentTime + 2)))
                throw new InvalidTimestampException(timestamp, currentTime);

            String[] hostnameParts = ipData.split(":");
            String host = hostnameParts[0];
            int port = Integer.parseInt(hostnameParts[1]);

            String reconstructedPayload = hostname + "///" + host + ":" + port + "///" + timestamp;

            if (!signatureVerifier.verify(reconstructedPayload, signature))
                throw new SigningVerificationFailureException();

            InetSocketAddress newIP = new InetSocketAddress(host, port);
            playerAbstraction.setIP(newIP);

            if (extraData != null) hostname = hostname + extraData;

            packetAbstraction.modifyOriginalPacket(hostname);
        } catch (InvalidTimestampException e) {
            handleInvalidTimestamp(playerAbstraction, e.getTimestamp(), e.getCurrentTime());
        } catch (SigningVerificationFailureException e) {
            handleSigningVerificationFailure(playerAbstraction, rawPayload);
        } catch (ConnectionNotProxiedException e) {
            handleNotProxiedConnection(playerAbstraction, rawPayload);
        } catch (IPModificationFailureException e) {
            this.logger.warning(String.format("%s[%s/%s]'s IP failed to be modified. Raw payload = \"%s\"", playerAbstraction.getName(), playerAbstraction.getUUID(), playerAbstraction.getIP(), rawPayload));
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleInvalidTimestamp(PlayerAbstraction playerAbstraction, long timestamp, long currentTime) {
        if (configAbstraction.isDebug()) {
            this.logger.warning(String.format("%s[%s/%s] provided valid handshake information, but timestamp was not valid. " +
                    "Provided timestamp: %d vs. system timestamp: %d. Please check your machine time.", playerAbstraction.getName(), playerAbstraction.getUUID(), playerAbstraction.getIP(), timestamp, currentTime));
        }

        if (configAbstraction.isOnlyProxy()) {
            playerAbstraction.disconnect();
        }
    }

    private void handleSigningVerificationFailure(PlayerAbstraction playerAbstraction, String rawPayload) {
        if (configAbstraction.isDebug()) {
            this.logger.warning(String.format("%s[%s/%s] provided valid handshake information, but signing check failed. Raw payload = \"%s\"", playerAbstraction.getName(), playerAbstraction.getUUID(), playerAbstraction.getIP(), rawPayload));
        }

        if (configAbstraction.isOnlyProxy()) {
            playerAbstraction.disconnect();
        }
    }

    private void handleNotProxiedConnection(PlayerAbstraction playerAbstraction, String rawPayload) {
        if (!configAbstraction.isOnlyProxy()) return;

        if (configAbstraction.isDebug()) {
            this.logger.info(String.format("%s[%s/%s] was disconnected because no proxy info was received and only-allow-proxy-connections is enabled. Raw payload = \"%s\"", playerAbstraction.getName(), playerAbstraction.getUUID(), playerAbstraction.getIP(), rawPayload));
        }

        playerAbstraction.disconnect();
    }
}
