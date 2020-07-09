package net.tcpshield.tcpshield.bungee;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class TCPShieldBungee extends Plugin implements Listener {

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerListener(this, new BungeeHandshakePacketHandler(this));
    }
}
