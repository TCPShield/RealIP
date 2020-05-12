package net.tcpshield.tcpshield.abstraction;

public interface IPacket {

    String getRawPayload();

    void modifyOriginalPacket(String hostname) throws Exception;

}
