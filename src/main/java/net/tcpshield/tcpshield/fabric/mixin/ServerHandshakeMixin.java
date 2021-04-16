package net.tcpshield.tcpshield.fabric.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.tcpshield.tcpshield.abstraction.IPacket;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.fabric.TCPShieldFabric;
import net.tcpshield.tcpshield.fabric.impl.FabricPacketImpl;
import net.tcpshield.tcpshield.fabric.impl.FabricPlayerImpl;
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
        IPacket packet = new FabricPacketImpl(handshake);
        IPlayer player = new FabricPlayerImpl(handshake, connection);

        TCPShieldFabric.PACKET_HANDLER.onHandshake(packet, player);
    }
}
