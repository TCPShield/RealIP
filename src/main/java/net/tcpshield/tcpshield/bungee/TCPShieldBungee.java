package net.tcpshield.tcpshield.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.tcpshield.tcpshield.ReflectionUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPShieldBungee extends Plugin implements Listener {

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerListener(this, new BungeeHandshakePacketHandler(this));

        Logger logger = ProxyServer.getInstance().getLogger();
        Logger newLogger = new Logger("BungeeCord", null) {
            public void log(Level level, String msg, Object param1) {
                if ((msg.equals("{0} has connected")) && (param1.getClass().getSimpleName().equals("InitialHandler"))) {
                    return;
                }
                super.log(level, msg, param1);
            }
        };

        newLogger.setParent(logger);
        try {
            ReflectionUtils.setField(ProxyServer.getInstance(), "logger", newLogger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
