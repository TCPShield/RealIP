package net.tcpshield.tcpshield.validation.cidr;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CIDRChecker {

    private final List<CIDRMatcher> cidrMatchers = new ArrayList<>();
    private final Set<String> cache = new HashSet<>(); // quick cache for already verified IPs; HashSet is O(1) on HashSet#contains() operations

    public CIDRChecker(List<String> cidrList) {
        for (String cidr : cidrList) {
            CIDRMatcher cidrMatcher = new CIDRMatcher(cidr);
            cidrMatchers.add(cidrMatcher);
        }
    }

    public boolean check(InetAddress inetAddress) {
        String ip = inetAddress.getHostAddress();
        if (cache.contains(ip)) return true;

        if (!checkUncached(inetAddress)) return false;

        cache.add(ip);
        return true;
    }

    private boolean checkUncached(InetAddress inetAddress) {
        for (CIDRMatcher cidrMatcher : cidrMatchers) {
            if (cidrMatcher.matches(inetAddress)) return true;
        }

        return false;
    }
}
