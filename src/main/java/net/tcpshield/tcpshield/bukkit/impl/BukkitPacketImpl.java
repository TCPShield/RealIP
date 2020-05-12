package net.tcpshield.tcpshield.bukkit.impl;

import com.comphenix.protocol.events.PacketContainer;
import net.tcpshield.tcpshield.abstraction.IPacket;

public class BukkitPacketImpl implements IPacket {

    private final PacketContainer packetContainer;
    private final String rawPayload;

    public BukkitPacketImpl(PacketContainer packetContainer) {
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
