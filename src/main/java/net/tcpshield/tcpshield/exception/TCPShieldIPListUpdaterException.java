package net.tcpshield.tcpshield.exception;

public class TCPShieldIPListUpdaterException extends RuntimeException {

    public TCPShieldIPListUpdaterException(Throwable cause) {
        super(cause);
    }

    public TCPShieldIPListUpdaterException(String message, Throwable cause) {
        super(message, cause);
    }

    public TCPShieldIPListUpdaterException(String message) {
        super(message);
    }
}
