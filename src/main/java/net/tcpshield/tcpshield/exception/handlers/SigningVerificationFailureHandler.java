package net.tcpshield.tcpshield.exception.handlers;

import lombok.AllArgsConstructor;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

import java.util.logging.Logger;

@AllArgsConstructor
public class SigningVerificationFailureHandler {

    private static final String SIGNING_FAILED = (
        "%s[%s/%s] provided valid handshake information, but signing check failed. " +
            "Raw payload = '%s'"
    );

    private final TCPShieldConfig config;
    private final Logger logger;

    public void handle(IPlayer player, String rawPayload) {
        if (config.isDebug()) debug(player, rawPayload);
        if (config.isOnlyProxy()) player.disconnect();
    }

    private void debug(IPlayer player, String rawPayload) {
        String message = String.format(
            SIGNING_FAILED,
            player.getName(), player.getUUID(), player.getIP(), rawPayload
        );
        logger.warning(message);
    }

}