package net.tcpshield.tcpshield.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MalformedPayloadException extends ConnectionNotProxiedException {

    public MalformedPayloadException(Throwable cause) {
        super(cause);
    }

    public MalformedPayloadException(String s) {
        super(s);
    }

}
