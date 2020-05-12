package net.tcpshield.tcpshield.bukkit.impl;

import com.comphenix.protocol.events.PacketContainer;
import net.tcpshield.tcpshield.abstraction.PacketAbstraction;

public class BukkitPacketAbstraction implements PacketAbstraction {

    private final PacketContainer packetContainer;
    private final String rawPayload;

    public BukkitPacketAbstraction(PacketContainer packetContainer) {
        this.packetContainer = packetContainer;
        this.rawPayload = packetContainer.getStrings().read(0);
    }

    @Override
    public String getRawPayload() {
        return rawPayload;
    }

    @Override
    public void modifyOriginalPacket(String hostname) {
        packetContainer.getStrings().write(0, hostname);
    }
}
