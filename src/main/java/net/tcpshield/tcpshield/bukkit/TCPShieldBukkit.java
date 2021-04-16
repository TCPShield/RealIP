package net.tcpshield.tcpshield.bukkit;

import net.tcpshield.tcpshield.bukkit.paper.TCPShieldPaper;
import net.tcpshield.tcpshield.bukkit.protocollib.TCPShieldProtocolLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        if (isPaper()) {
            new TCPShieldPaper(this).load();
        } else {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
                this.getLogger().warning("TCPShield not loading because ProtocolLib is not installed. Either use Paper (1.16 build #503 or higher) to enable native compatibility or install ProtocolLib.");
                return;
            }

            new TCPShieldProtocolLib(this).load();
        }
    }

    /**
     * Checks if the server version supports native Paper support (Paper 1.16.5 build #503 or higher)
     *
     * @return if the server version supports native Paper support
     */
    private boolean isPaper() {
        try {
            Class<?> handshakeEvent = Class.forName("com.destroystokyo.paper.event.player.PlayerHandshakeEvent");
            handshakeEvent.getMethod("getOriginalSocketAddressHostname");
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }
}
