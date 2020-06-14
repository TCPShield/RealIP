package net.tcpshield.tcpshield.bukkit;

import net.tcpshield.tcpshield.bukkit.paper.TCPShieldPaper;
import net.tcpshield.tcpshield.bukkit.protocollib.TCPShieldProtocolLib;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        if (isPaper()) {
            new TCPShieldPaper(this).load();
        } else {
            new TCPShieldProtocolLib(this).load();
        }
    }

    private boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerHandshakeEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
