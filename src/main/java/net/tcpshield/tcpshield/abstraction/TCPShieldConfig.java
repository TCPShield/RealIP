package net.tcpshield.tcpshield.abstraction;

import java.io.File;

public abstract class TCPShieldConfig {

    protected boolean onlyProxy;
    protected File ipWhitelistFolder;
    protected boolean debug;
    protected boolean geyser;

    public boolean isOnlyProxy() {
        return this.onlyProxy;
    }

    public File getIPWhitelistFolder() {
        return ipWhitelistFolder;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public boolean isGeyser() {
        return this.geyser;
    }

}
