package net.tcpshield.tcpshield.abstraction;

import lombok.Getter;

public abstract class TCPShieldConfig {

    @Getter protected boolean onlyProxy;
    @Getter protected String timestampValidationMode;
    @Getter protected boolean debug;

}
