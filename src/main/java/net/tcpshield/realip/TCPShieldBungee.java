package net.tcpshield.realip;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class TCPShieldBungee
        extends Plugin
        implements Listener {
    private boolean onlyProxy;

    public void onEnable() {
        saveDefaultConfig();
        Configuration config = loadConfig();
        try {
            Signing.init();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            getLogger().severe("Couldn't initialize signing module.");
            throw new RuntimeException(e);
        }
        this.onlyProxy = config.getBoolean("only-allow-proxy-connections");

        ProxyServer proxy = getProxy();
        PluginManager pluginManager = proxy.getPluginManager();
        pluginManager.registerListener(this, this);

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
            set(ProxyServer.getInstance(), "logger", newLogger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Configuration loadConfig() {
        try {
            ConfigurationProvider configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            File configFile = getConfigFile();
            return configProvider.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load config", e);
        }
    }

    private void saveDefaultConfig() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File file = getConfigFile();
        if (!file.exists()) {
            try {
                InputStream in = getResourceAsStream("config.yml");
                Throwable localThrowable3 = null;
                try {
                    Files.copy(in, file.toPath(), new CopyOption[0]);
                } catch (Throwable localThrowable1) {
                    localThrowable3 = localThrowable1;
                    throw localThrowable1;
                } finally {
                    if (in != null) {
                        if (localThrowable3 != null) {
                            try {
                                in.close();
                            } catch (Throwable localThrowable2) {
                                localThrowable3.addSuppressed(localThrowable2);
                            }
                        } else {
                            in.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getConfigFile() {
        File dataFolder = getDataFolder();
        return new File(dataFolder, "config.yml");
    }

    @EventHandler(priority = -64)
    public void onPlayerHandshake(PlayerHandshakeEvent e) {
        boolean proxyConnection = false;
        Channel channel = null;
        try {
            Field wrapperField = e.getConnection().getClass().getDeclaredField("ch");
            wrapperField.setAccessible(true);
            Object wrapper = wrapperField.get(e.getConnection());
            wrapperField.setAccessible(false);

            Field chField = wrapper.getClass().getDeclaredField("ch");
            chField.setAccessible(true);
            Channel ch = (Channel) chField.get(wrapper);
            chField.setAccessible(false);
            channel = ch;
            if (e.getHandshake().getHost().contains("//")) {
                String raw = e.getHandshake().getHost();

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
                        if (!Signing.verify(reconstructedPayload.getBytes(StandardCharsets.UTF_8), signature)) {
                            throw new Exception("Couldn't verify signature.");
                        }

                        try {
                            set(e.getHandshake(), "host", hostname);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        
                        try {
                            set(e.getHandshake(), "port", port);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        PendingConnection connection = e.getConnection();

                        InetSocketAddress sockadd = new InetSocketAddress(host, port);
                        proxyConnection = true;
                        try {
                            set(wrapper.getClass(), wrapper, "remoteAddress", sockadd);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            set(AbstractChannel.class, ch, "remoteAddress", sockadd);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            set(AbstractChannel.class, ch, "localAddress", sockadd);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        InetSocketAddress virtualHost = new InetSocketAddress(hostname, e.getHandshake().getPort());
                        try {
                            set(connection, "virtualHost", virtualHost);
                        } catch (Exception ex) {
                            try {
                                set(connection, "vHost", virtualHost);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception localException) {
            Logger logger;
            PendingConnection connection;
            localException.printStackTrace();
        } finally {
            if ((this.onlyProxy) &&
                    (!proxyConnection)) {
                Logger logger = getLogger();
                PendingConnection connection = e.getConnection();
                logger.warning("Disconnecting " + connection.getAddress() + " because no proxy info was received and only-allow-proxy-connections is enabled.");
                if (channel != null) {
                    channel.flush().close();
                } else {
                    connection.disconnect(new BaseComponent[0]);
                }
            }
        }
    }

    protected static void set(Object main_object, String field, Object value)
            throws Exception {
        Field f = main_object.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(main_object, value);
    }

    protected static void set(Class<?> c, Object main_object, String field, Object value)
            throws Exception {
        Field f = c.getDeclaredField(field);
        f.setAccessible(true);
        f.set(main_object, value);
    }
}
