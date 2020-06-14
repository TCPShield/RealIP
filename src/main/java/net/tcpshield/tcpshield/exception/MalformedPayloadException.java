package net.tcpshield.tcpshield.exception;

public class MalformedPayloadException extends ConnectionNotProxiedException {

    public MalformedPayloadException() {
    }

    public MalformedPayloadException(Throwable cause) {
        super(cause);
    }

    public MalformedPayloadException(String s) {
        super(s);
    }
}
