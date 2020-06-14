package net.tcpshield.tcpshield.bukkit.paper.impl;

import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import lombok.AllArgsConstructor;
import net.tcpshield.tcpshield.abstraction.IPacket;

@AllArgsConstructor
public class PaperPacketImpl implements IPacket {

    private final PlayerHandshakeEvent handshakeEvent;

    @Override
    public String getRawPayload() {
        return handshakeEvent.getOriginalHandshake();
    }

    @Override
    public void modifyOriginalPacket(String hostname) {
        handshakeEvent.setCancelled(false);
        handshakeEvent.setServerHostname(hostname);
    }

}
