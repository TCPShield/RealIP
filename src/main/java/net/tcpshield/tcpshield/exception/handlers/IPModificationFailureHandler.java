package net.tcpshield.tcpshield.exception.handlers;

import lombok.AllArgsConstructor;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

import java.util.logging.Logger;

@AllArgsConstructor
public class IPModificationFailureHandler {

    private static final String FAILED_TO_CHANGE_ADDRESS = (
        "%s[%s/%s]'s IP failed to be modified. Raw payload = '%s'"
    );

    private final TCPShieldConfig config;
    private final Logger logger;

    public void handle(IPlayer player, String rawPayload) {
        if (!config.isOnlyProxy()) return;

        if (config.isDebug()) debug(player, rawPayload);
        player.disconnect();
    }

    private void debug(IPlayer player, String rawPayload) {
        String message = String.format(
            FAILED_TO_CHANGE_ADDRESS,
            player.getName(), player.getUUID(), player.getIP(), rawPayload
        );
        logger.warning(message);
    }

}