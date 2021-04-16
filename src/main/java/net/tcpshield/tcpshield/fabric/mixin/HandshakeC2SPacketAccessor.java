package net.tcpshield.tcpshield.fabric.mixin;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandshakeC2SPacket.class)
public interface HandshakeC2SPacketAccessor {
    @Accessor
    String getAddress();
}
