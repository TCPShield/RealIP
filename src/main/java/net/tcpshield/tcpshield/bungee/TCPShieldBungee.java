package net.tcpshield.tcpshield.bungee;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.tcpshield.tcpshield.HandshakePacketHandler;
import net.tcpshield.tcpshield.bungee.impl.BungeeConfigImpl;
import net.tcpshield.tcpshield.geyser.GeyserUtils;

public class TCPShieldBungee extends Plugin implements Listener {

    @Override
    public void onEnable() {
        HandshakePacketHandler handshakePacketHandler = new HandshakePacketHandler(this.getLogger(), new BungeeConfigImpl(this));

        GeyserUtils.initGeyserOrDefault(handshakePacketHandler, () -> {
            PluginManager pluginManager = this.getProxy().getPluginManager();
            pluginManager.registerListener(this, new BungeeHandshakePacketHandler(handshakePacketHandler));
        });
    }
}
