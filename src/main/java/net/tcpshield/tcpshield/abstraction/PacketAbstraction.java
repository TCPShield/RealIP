package net.tcpshield.tcpshield.abstraction;

public interface PacketAbstraction {

    String getRawPayload();

    void modifyOriginalPacket(String hostname) throws Exception;

}
