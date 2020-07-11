package net.tcpshield.tcpshield.exception;

public class TCPShieldInitializationException extends RuntimeException {

    public TCPShieldInitializationException(Throwable cause) {
        super(cause);
    }

    public TCPShieldInitializationException(String message) {
        super(message);
    }
}
