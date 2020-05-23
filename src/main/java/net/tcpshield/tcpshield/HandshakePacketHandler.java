package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import net.tcpshield.tcpshield.exception.*;
import net.tcpshield.tcpshield.validation.TimestampValidation;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class HandshakePacketHandler {

    private final Logger logger;
    private final SignatureVerifier signatureVerifier;
    private final TimestampValidation timestampValidation;
    private final TCPShieldConfig config;

    public HandshakePacketHandler(Logger logger, TCPShieldConfig config) {
        try {
            this.logger = logger;
            this.signatureVerifier = new SignatureVerifier();
            this.timestampValidation = new TimestampValidation(config);
            this.config = config;
        } catch (Exception e) {
            throw new TCPShieldInitializationException(e);
        }
    }

    public void onHandshake(IPacket packet, IPlayer player) {
        String rawPayload = packet.getRawPayload();

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

            if (!timestampValidation.checkTimestamp(timestamp))
                throw new InvalidTimestampException(timestamp, timestampValidation.getTime());

            String[] hostnameParts = ipData.split(":");
            String host = hostnameParts[0];
            int port = Integer.parseInt(hostnameParts[1]);

            String reconstructedPayload = hostname + "///" + host + ":" + port + "///" + timestamp;

            if (!signatureVerifier.verify(reconstructedPayload, signature))
                throw new SigningVerificationFailureException();

            InetSocketAddress newIP = new InetSocketAddress(host, port);
            player.setIP(newIP);

            if (extraData != null) hostname = hostname + extraData;

            packet.modifyOriginalPacket(hostname);
        } catch (InvalidTimestampException e) {
            handleInvalidTimestamp(player, e.getTimestamp(), e.getCurrentTime());
        } catch (SigningVerificationFailureException e) {
            handleSigningVerificationFailure(player, rawPayload);
        } catch (ConnectionNotProxiedException e) {
            handleNotProxiedConnection(player, rawPayload);
        } catch (IPModificationFailureException e) {
            this.logger.warning(String.format("%s[%s/%s]'s IP failed to be modified. Raw payload = \"%s\"", player.getName(), player.getUUID(), player.getIP(), rawPayload));
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleInvalidTimestamp(IPlayer player, long timestamp, long currentTime) {
        if (config.isDebug()) {
            this.logger.warning(String.format("%s[%s/%s] provided valid handshake information, but timestamp was not valid. " +
                    "Provided timestamp: %d vs. system timestamp: %d. Please check your machine time.", player.getName(), player.getUUID(), player.getIP(), timestamp, currentTime));
        }

        if (config.isOnlyProxy()) {
            player.disconnect();
        }
    }

    private void handleSigningVerificationFailure(IPlayer player, String rawPayload) {
        if (config.isDebug()) {
            this.logger.warning(String.format("%s[%s/%s] provided valid handshake information, but signing check failed. Raw payload = \"%s\"", player.getName(), player.getUUID(), player.getIP(), rawPayload));
        }

        if (config.isOnlyProxy()) {
            player.disconnect();
        }
    }

    private void handleNotProxiedConnection(IPlayer player, String rawPayload) {
        if (!config.isOnlyProxy()) return;

        if (config.isDebug()) {
            this.logger.info(String.format("%s[%s/%s] was disconnected because no proxy info was received and only-allow-proxy-connections is enabled. Raw payload = \"%s\"", player.getName(), player.getUUID(), player.getIP(), rawPayload));
        }

        player.disconnect();
    }
}
