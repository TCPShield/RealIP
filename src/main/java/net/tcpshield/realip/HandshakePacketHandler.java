package net.tcpshield.realip;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Handshake.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.server.SocketInjector;
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
import com.comphenix.protocol.reflect.StructureModifier;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HandshakePacketHandler extends PacketAdapter {
    private String properField = null;
    private final Logger logger;
    private final boolean onlyProxy;
    private final boolean debugMode;

    public HandshakePacketHandler(Logger logger, boolean onlyProxy, boolean debugMode) {
        super(TCPShieldBukkit.getInstance(), new PacketType[]{PacketType.Handshake.Client.SET_PROTOCOL});
        this.logger = logger;
        this.onlyProxy = onlyProxy;
        this.debugMode = debugMode;
    }

    public void onPacketReceiving(PacketEvent event) {
        boolean proxyConnection = false;
        String raw = null;
        try {
            raw = (String) event.getPacket().getStrings().read(0);

            String[] payload = raw.split("///", 3);
            if (payload.length >= 3) {
                String hostname = payload[0];
                String ipData = payload[1];
                String[] ts_sig = payload[2].split("///", 2);
                if (ts_sig.length >= 2) {
                    int timestamp = Integer.parseInt(ts_sig[0]);
                    String signature = ts_sig[1];

                    String[] hostnameParts = ipData.split(":");
                    String host = hostnameParts[0];
                    int port = Integer.parseInt(hostnameParts[1]);

                    String reconstructedPayload = hostname + "///" + host + ":" + port + "///" + timestamp;

                    if (signature.contains("%%%")) {
                        signature = signature.split("%%%", 2)[0];
                    }

                    if (!Signing.verify(reconstructedPayload.getBytes(StandardCharsets.UTF_8), signature)) {
                        throw new Exception("Couldn't verify signature.");
                    }
                    try {
                        SocketInjector ignored = TemporaryPlayerFactory.getInjectorFromPlayer(event.getPlayer());
                        Object injector = ReflectionUtils.getPrivateField(ignored.getClass(), ignored, "injector");
                        Object networkManager = ReflectionUtils.getPrivateField(injector.getClass(), injector, "networkManager");
                        if (this.properField == null) {
                            this.properField = ReflectionUtils.getProperField(networkManager.getClass());
                        }
                        Channel channel = (Channel) ReflectionUtils.getPrivateField(injector.getClass(), injector, "originalChannel");
                        InetSocketAddress newRemoteAddress = new InetSocketAddress(host, port);
                        proxyConnection = true;
                        try {
                            ReflectionUtils.setFinalField(networkManager.getClass(), networkManager, this.properField == null ? "l" : this.properField, newRemoteAddress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ReflectionUtils.setFinalField(AbstractChannel.class, channel, "remoteAddress", newRemoteAddress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    event.getPacket().getStrings().write(0, hostname);
                }
            }
        } catch (Exception ex) {
            Player player; ;
        } finally {
            if ((this.onlyProxy) && (!proxyConnection)) {
                Player player = event.getPlayer();

                if(this.debugMode) {
                    this.logger.warning("Disconnecting " + player.getAddress() + " because no proxy info was received and only-allow-proxy-connections is enabled.");
                }

                if(raw != null) {
                    this.logger.warning(raw);
                }
                player.kickPlayer("");
            }
        }
    }

    private Channel getChannel(Player player) {
        try {
            Class<? extends Player> playerClass = player.getClass();
            Object playerHandle = playerClass.getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
            Object playerConnection = getNmsClass("EntityPlayer").getField("playerConnection").get(playerHandle);
            Object networkManager = getNmsClass("PlayerConnection").getField("networkManager").get(playerConnection);
            return (Channel) getNmsClass("NetworkManager").getField("channel").get(networkManager);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get player channel", e);
        }
    }

    private Class getNmsClass(String name)
            throws ClassNotFoundException {
        return Class.forName(getNmsPackage() + "." + name);
    }

    private String getNmsPackage() {
        String obcString = Bukkit.getServer().getClass().getPackage().getName();
        String versionString = obcString.substring(obcString.lastIndexOf('.') + 1);
        return "net.minecraft.server." + versionString;
    }

    public static class ReflectionUtils {
        private static final Field FIELD_MODIFIERS;
        private static Map<Class<?>, Map<String, Field>> cachedFields = new HashMap();

        public static void setFinalField(Class objectClass, Object object, String fieldName, Object value)
                throws Exception {
            Field field = getDeclaredField(objectClass, fieldName);
            field.setAccessible(true);
            if (Modifier.isFinal(field.getModifiers())) {
                FIELD_MODIFIERS.setInt(field, field.getModifiers() & 0xFFFFFFEF);
            }
            field.set(object, value);
        }

        public static Object getPrivateField(Class objectClass, Object object, String fieldName)
                throws Exception {
            Field field = getDeclaredField(objectClass, fieldName);
            field.setAccessible(true);
            return field.get(object);
        }

        public static String getProperField(Class objectClass) {
            for (Field f : objectClass.getFields()) {
                if (f.getType() == SocketAddress.class) {
                    return f.getName();
                }
            }
            return "N/A";
        }

        private static Field getDeclaredField(Class<?> clazz, String fieldName) {
            if (!cachedFields.containsKey(clazz)) {
                cachedFields.put(clazz, new HashMap());
            }
            Map<String, Field> clazzCache = (Map) cachedFields.get(clazz);
            if (clazzCache.containsKey(fieldName)) {
                return (Field) clazzCache.get(fieldName);
            }
            try {
                Field field = clazz.getDeclaredField(fieldName);
                clazzCache.put(fieldName, field);
                return field;
            } catch (NoSuchFieldException e) {
                clazzCache.put(fieldName, null);
                throw new RuntimeException(e);
            }
        }

        static {
            Field field = null;
            try {
                field = getDeclaredField(Field.class, "modifiers");
                field.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FIELD_MODIFIERS = field;
        }
    }
}
