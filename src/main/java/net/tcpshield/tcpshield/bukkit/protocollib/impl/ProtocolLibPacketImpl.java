package net.tcpshield.tcpshield.bukkit.protocollib.impl;

import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import net.tcpshield.tcpshield.abstraction.IPacket;

public class ProtocolLibPacketImpl implements IPacket {

    private final PacketContainer packetContainer;
    @Getter private final String rawPayload;

    public ProtocolLibPacketImpl(PacketContainer packetContainer) {
        this.packetContainer = packetContainer;
        this.rawPayload = packetContainer.getStrings().read(0);
    }

    @Override
    public void modifyOriginalPacket(String hostname) {
        packetContainer.getStrings().write(0, hostname);
    }

}
