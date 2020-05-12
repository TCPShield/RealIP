package net.tcpshield.tcpshield.bukkit.impl;

import com.comphenix.protocol.injector.server.SocketInjector;
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
import io.netty.channel.AbstractChannel;
import net.tcpshield.tcpshield.ReflectionUtils;
import net.tcpshield.tcpshield.abstraction.IPlayer;
import net.tcpshield.tcpshield.exception.IPModificationFailureException;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class BukkitPlayerImpl implements IPlayer {

    private static Field socketAddressField;
    private final Player player;
    private String ip;

    public BukkitPlayerImpl(Player player) {
        this.player = player;
        this.ip = player.getAddress().getAddress().getHostAddress();
    }

    @Override
    public String getUUID() {
        return "unknown"; // not supported with temporary players
    }

    @Override
    public String getName() {
        return "unknown"; // not supported with temporary players
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public void setIP(InetSocketAddress ip) throws IPModificationFailureException {
        this.ip = ip.getAddress().getHostAddress();

        try {
            SocketInjector ignored = TemporaryPlayerFactory.getInjectorFromPlayer(player);
            Object injector = ReflectionUtils.getObjectInPrivateField(ignored, "injector");
            Object networkManager = ReflectionUtils.getObjectInPrivateField(injector, "networkManager");
            if (socketAddressField == null) {
                socketAddressField = ReflectionUtils.searchFieldByClass(networkManager.getClass(), SocketAddress.class);
            }

            ReflectionUtils.setFinalField(networkManager, socketAddressField, ip);

            Object channel = ReflectionUtils.getObjectInPrivateField(injector, "originalChannel");
            ReflectionUtils.setFinalField(channel, ReflectionUtils.getDeclaredField(AbstractChannel.class, "remoteAddress"), ip);
        } catch (Exception e) {
            throw new IPModificationFailureException(e);
        }
    }

    @Override
    public void disconnect() {
        player.kickPlayer("");
    }
}
