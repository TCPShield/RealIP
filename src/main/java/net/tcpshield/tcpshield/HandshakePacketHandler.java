package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import net.tcpshield.tcpshield.exception.*;
import net.tcpshield.tcpshield.exception.handlers.IPModificationFailureHandler;
import net.tcpshield.tcpshield.exception.handlers.ConnectionNotProxiedHandler;
import net.tcpshield.tcpshield.exception.handlers.InvalidTimestampHandler;
import net.tcpshield.tcpshield.exception.handlers.SigningVerificationFailureHandler;
import net.tcpshield.tcpshield.validation.TimestampValidation;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class HandshakePacketHandler {

    private final Logger logger;
    private final SignatureVerifier signatureVerifier;
    private final TimestampValidation timestampValidation;
    private final TCPShieldConfig config;

    private final InvalidTimestampHandler invalidTimestampHandler;
    private final ConnectionNotProxiedHandler connectionNotProxiedHandler;
    private final SigningVerificationFailureHandler signingFailureHandler;
    private final IPModificationFailureHandler ipModificationFailureHandler;

    public HandshakePacketHandler(Logger logger, TCPShieldConfig config) {
        try {
            this.logger = logger;
            this.signatureVerifier = new SignatureVerifier();
            this.timestampValidation = new TimestampValidation(config);
            this.config = config;

            this.invalidTimestampHandler = new InvalidTimestampHandler(config, logger);
            this.connectionNotProxiedHandler = new ConnectionNotProxiedHandler(config, logger);
            this.signingFailureHandler = new SigningVerificationFailureHandler(config, logger);
            this.ipModificationFailureHandler = new IPModificationFailureHandler(config, logger);
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
            if (payload.length != 4) {
                throw new MalformedPayloadException("payload.length != 4. Raw payload = \"" + rawPayload + "\"");
            }

            String hostname = payload[0];
            String ipData = payload[1];
            int timestamp;
            try {
                timestamp = Integer.parseInt(payload[2]);
            } catch (NumberFormatException e) {
                throw new MalformedPayloadException(e);
            }
            String signature = payload[3];

            if (!timestampValidation.checkTimestamp(timestamp)) {
                throw new InvalidTimestampException(timestamp, timestampValidation.getTime());
            }

            String[] hostnameParts = ipData.split(":");
            String host = hostnameParts[0];
            int port = Integer.parseInt(hostnameParts[1]);

            String reconstructedPayload = hostname + "///" + host + ":" + port + "///" + timestamp;

            if (!signatureVerifier.verify(reconstructedPayload, signature)) {
                throw new SigningVerificationFailureException();
            }

            InetSocketAddress newIP = new InetSocketAddress(host, port);
            player.setIP(newIP);

            if (extraData != null) hostname = hostname + extraData;

            packet.modifyOriginalPacket(hostname);
        } catch (InvalidTimestampException e) {
            invalidTimestampHandler.handle(player, e.getTimestamp(), e.getCurrentTime());
        } catch (SigningVerificationFailureException e) {
            signingFailureHandler.handle(player, rawPayload);
        } catch (ConnectionNotProxiedException e) {
            connectionNotProxiedHandler.handle(player, rawPayload);
        } catch (IPModificationFailureException e) {
            ipModificationFailureHandler.handle(player, rawPayload);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
