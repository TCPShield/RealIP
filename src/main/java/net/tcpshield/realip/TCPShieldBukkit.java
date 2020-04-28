package net.tcpshield.realip;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import net.tcpshield.realip.util.TinyProtocol;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TCPShieldBukkit extends JavaPlugin implements Listener {
    private static TCPShieldBukkit INSTANCE;
    private static Boolean stopping = Boolean.valueOf(false);
    private boolean onlyProxy;
    private boolean debugMode;

    public void onEnable() {
        INSTANCE = this;
        try {
            Signing.init();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            getLogger().severe("Couldn't initialize signing module.");
            throw new RuntimeException(e);
        }

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        this.onlyProxy = config.getBoolean("only-allow-proxy-connections");
        this.debugMode = config.getBoolean("debug-mode");

        ProtocolLibrary.getProtocolManager().addPacketListener(new HandshakePacketHandler(getLogger(), this.onlyProxy, this.debugMode));

        TinyProtocol tp = new TinyProtocol(getInstance(), this.debugMode);

    }


    public void onDisable() {
        stopping = Boolean.valueOf(true);
        INSTANCE = null;
    }

    public static TCPShieldBukkit getInstance() {
        if (stopping.booleanValue()) {
            throw new IllegalAccessError("Plugin is disabling!");
        }
        return INSTANCE;
    }
}
