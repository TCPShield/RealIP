package net.tcpshield.tcpshield.validation.cidr;

import net.tcpshield.tcpshield.exception.TCPShieldInitializationException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CIDRMatcher {

    private final int maskBits;
    private final int maskBytes;
    private final boolean simpleCIDR;
    private final InetAddress cidrAddress;

    public CIDRMatcher(String ipAddress) {
        String[] split = ipAddress.split("/");

        String parsedIPAddress;
        if (split.length != 0) {
            parsedIPAddress = split[0];

            this.maskBits = Integer.parseInt(split[1]);
            this.simpleCIDR = maskBits == 32;
        } else {
            parsedIPAddress = ipAddress;

            this.maskBits = -1;
            this.simpleCIDR = true;
        }

        this.maskBytes = simpleCIDR ? -1 : maskBits / 8;

        try {
            cidrAddress = InetAddress.getByName(parsedIPAddress);
        } catch (UnknownHostException e) {
            throw new TCPShieldInitializationException(e);
        }
    }

    public boolean matches(InetAddress inetAddress) {
        if (!cidrAddress.getClass().equals(inetAddress.getClass())) return false; // check if IP is IPv4 or IPv6

        if (simpleCIDR) return inetAddress.equals(cidrAddress); // check for equality if it's a simple CIDR

        byte[] inetAddressBytes = inetAddress.getAddress();
        byte[] requiredAddressBytes = cidrAddress.getAddress();

        byte finalByte = (byte) (0xFF00 >> (maskBits & 0x07));

        for (int i = 0; i < maskBytes; i++) {
            if (inetAddressBytes[i] != requiredAddressBytes[i]) return false;
        }

        if (finalByte != 0)
            return (inetAddressBytes[maskBytes] & finalByte) == (requiredAddressBytes[maskBytes] & finalByte);

        return true;
    }
}
