package net.tcpshield.tcpshield.bukkit;

import net.tcpshield.tcpshield.bukkit.paper.TCPShieldPaper;
import net.tcpshield.tcpshield.bukkit.protocollib.TCPShieldProtocolLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("enable");

        if (isPaper()) {
            new TCPShieldPaper(this).load();
        } else {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
                this.getLogger().warning("TCPShield not loading because ProtocolLib is not installed. Either use Paper to enable native compatibility or install ProtocolLib.");
                return;
            }

            new TCPShieldProtocolLib(this).load();
        }
    }

    private boolean isPaper() {
        if (1 == 1) return false;

        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerHandshakeEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
