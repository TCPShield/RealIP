package net.tcpshield.tcpshield.validation;

import net.tcpshield.tcpshield.abstraction.TCPShieldConfig;
import net.tcpshield.tcpshield.exception.TCPShieldInitializationException;
import net.tcpshield.tcpshield.validation.cidr.CIDRChecker;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;

public class IPValidation {

    private final TCPShieldConfig config;
    private final IPListUpdater ipListUpdater;
    private final CIDRChecker cidrChecker;

    private List<String> ipWhitelist = new ArrayList<>();

    public IPValidation(TCPShieldConfig config, Logger logger) {
        this.config = config;
        this.ipListUpdater = new IPListUpdater(config, this, logger);
        new IPListAutoUpdater(ipListUpdater).init();

        checkIPList();

        try {
            readIPWhitelist();
            this.cidrChecker = new CIDRChecker(ipWhitelist);
        } catch (IOException e) {
            throw new TCPShieldInitializationException(e);
        }
    }

    public boolean validateIP(InetAddress inetAddress) {
        return cidrChecker.check(inetAddress);
    }

    private List<String> getIPWhitelist() throws IOException {
        List<String> ipWhitelist = new ArrayList<>();

        for (File file : Objects.requireNonNull(config.getIPWhitelistFolder().listFiles())) {
            if (file.isDirectory()) continue;

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String cidrEntry = scanner.nextLine();
                    ipWhitelist.add(cidrEntry);
                }
            }
        }

        return ipWhitelist;
    }

    public void readIPWhitelist() throws IOException {
        this.ipWhitelist = getIPWhitelist();
    }

    private void checkIPList() {
        if (config.getIPWhitelistFolder().mkdirs()) {
            ipListUpdater.update();
        }
    }
}
