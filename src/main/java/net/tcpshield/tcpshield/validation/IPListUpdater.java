package net.tcpshield.tcpshield.validation;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import net.tcpshield.tcpshield.exception.TCPShieldIPListUpdaterException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IPListUpdater {

    private final TCPShieldConfig config;
    private final IPValidation ipValidation;
    private final Logger logger;

    public IPListUpdater(TCPShieldConfig config, IPValidation ipValidation, Logger logger) {
        this.config = config;
        this.ipValidation = ipValidation;
        this.logger = logger;
    }

    public void update() {
        try {
            String ipList = getIPList();

            File ipWhitelistFolder = config.getIPWhitelistFolder();
            File tcpShieldIPList = new File(ipWhitelistFolder, "tcpshield-ips.list");

            if (tcpShieldIPList.createNewFile()) {
                logger.info("[TCPShield] Fetched updated IP Whitelist from https://tcpshield.com/v4/");
            }

            try (FileWriter writer = new FileWriter(tcpShieldIPList, false)) {
                writer.write(ipList);
            }

            ipValidation.readIPWhitelist();
        } catch (Exception e) {
            throw new TCPShieldIPListUpdaterException(e);
        }
    }

    public String getIPList() throws IOException {
        URL url = new URL("https://tcpshield.com/v4/");

        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();

            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                int responseCode = connection.getResponseCode();
                if (responseCode != 200)
                    throw new TCPShieldIPListUpdaterException("https://tcpshield.com/v4 did not return status code 200: " + responseCode);

                return bufferedReader.lines().filter(str -> !str.isEmpty()).collect(Collectors.joining("\n"));
            }
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
}
