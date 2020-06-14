package net.tcpshield.tcpshield.abstraction;

public abstract class TCPShieldConfig {

    protected boolean onlyProxy;
    protected String timestampValidationMode;
    protected boolean debug;

    public boolean isOnlyProxy() {
        return this.onlyProxy;
    }

    public String getTimestampValidationMode() {
        return this.timestampValidationMode;
    }

    public boolean isDebug() {
        return this.debug;
    }

}
