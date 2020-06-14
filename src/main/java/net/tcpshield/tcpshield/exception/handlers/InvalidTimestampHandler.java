package net.tcpshield.tcpshield.exception.handlers;

import lombok.AllArgsConstructor;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

import java.util.logging.Logger;

@AllArgsConstructor
public class InvalidTimestampHandler {

    private static final String INVALID_TIMESTAMP = (
        "%s[%s/%s] provided valid handshake information, but timestamp was not valid. " +
            "Provided timestamp: %d vs. system timestamp: %d. Please check your machine time."
    );

    private final TCPShieldConfig config;
    private final Logger logger;

    public void handle(IPlayer player, long timestamp, long currentTime) {
        if (config.isDebug()) debug(player, timestamp, currentTime);
        if (config.isOnlyProxy()) player.disconnect();
    }

    private void debug(IPlayer player, long timestamp, long currentTime) {
        String message = String.format(
            INVALID_TIMESTAMP,
            player.getName(), player.getUUID(), player.getIP(), timestamp, currentTime
        );
        logger.warning(message);
    }

}