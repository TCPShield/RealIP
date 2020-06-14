package net.tcpshield.tcpshield.exception.handlers;

import lombok.AllArgsConstructor;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;

import java.util.logging.Logger;

@AllArgsConstructor
public class ConnectionNotProxiedHandler {

    private static final String DISCONNECTED_NO_PROXY = (
        "%s[%s/%s] was disconnected because no proxy info was received and only-allow-proxy-connections is enabled. " +
            "Raw payload = '%s'"
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
            DISCONNECTED_NO_PROXY,
            player.getName(), player.getUUID(), player.getIP(), rawPayload
        );
        logger.warning(message);
    }

}