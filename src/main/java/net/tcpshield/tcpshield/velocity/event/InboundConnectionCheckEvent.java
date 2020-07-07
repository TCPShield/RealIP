package net.tcpshield.tcpshield.velocity.event;

import com.velocitypowered.api.proxy.InboundConnection;

public class InboundConnectionCheckEvent {

    private final InboundConnection inboundConnection;
    private final boolean valid;

    public InboundConnectionCheckEvent(InboundConnection inboundConnection, boolean valid) {
        this.inboundConnection = inboundConnection;
        this.valid = valid;
    }

    public InboundConnection getInboundConnection() {
        return this.inboundConnection;
    }

    public boolean isValid() {
        return this.valid;
    }
}
