package net.tcpshield.tcpshield;

public class TestConstants {

    public static final String STANDARD_HOSTNAME = "example.org";

    public static final String PAYLOAD_HOSTNAME = "homeserver.fuzzlemann.de";
    public static final String PAYLOAD_FML_HOSTNAME = "homeserver.fuzzlemann.de\0FML\0";
    public static final String PAYLOAD_IP = "79.227.33.179";

    public static final String VALID_PAYLOAD_INVALID_TIMESTAMP = "homeserver.fuzzlemann.de///79.227.33.179:53462///1589573485///MGQCMHFac1If9Y8vGAkZMTVapmC6l4M7EfD0YJ0b3pQy73IFjEPsOQn+yxp9lAKSZz7X3AIwGJ2rSxGu4JvlW5sm0pyzqUrc6mfd0xHUWC9JkusrSBSKDfiFV7TCmSZc0gGivgTs";
    public static final String VALID_PAYLOAD_INVALID_TIMESTAMP_FML_TAGGED = VALID_PAYLOAD_INVALID_TIMESTAMP + "\0FML\0";
    public static final String INVALID_PAYLOAD_INVALID_SIGNATURE = VALID_PAYLOAD_INVALID_TIMESTAMP + "_INVALID_SIGNATURE";
    public static final String INVALID_PAYLOAD_MALFORMED_PAYLOAD = "MALFORMED_PAYLOAD";

}
