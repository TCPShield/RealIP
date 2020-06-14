package net.tcpshield.tcpshield.exception;

public class ConnectionNotProxiedException extends SecurityException {

    public ConnectionNotProxiedException() {
        super();
    }

    public ConnectionNotProxiedException(Throwable cause) {
        super(cause);
    }

    public ConnectionNotProxiedException(String s) {
        super(s);
    }

}
