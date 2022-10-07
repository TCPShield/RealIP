package net.tcpshield.tcpshield.fabric.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.tcpshield.tcpshield.fabric.TCPShieldFabric;
import net.tcpshield.tcpshield.fabric.impl.FabricPacket;
import net.tcpshield.tcpshield.fabric.impl.FabricPlayer;
import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeMixin {

    @Shadow @Final private ClientConnection connection;

    @Inject(
            method = "onHandshake",
            at = @At("HEAD"))
    private void onHandshake(HandshakeC2SPacket handshake, CallbackInfo ci) {
        PacketProvider packet = new FabricPacket(handshake);
        PlayerProvider player = new FabricPlayer(handshake, connection);

        TCPShieldFabric.packetHandler.handleHandshake(packet, player);
    }
}
