package net.tcpshield.tcpshield.abstraction;

import net.tcpshield.tcpshield.exception.IPModificationFailureException;

import java.net.InetSocketAddress;

public interface IPlayer {

    String getUUID();

    String getName();

    String getIP();

    void setIP(InetSocketAddress ip) throws IPModificationFailureException;

    void disconnect();

}
