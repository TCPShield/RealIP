package net.tcpshield.tcpshield;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureVerifier {

    private final PublicKey publicKey;

    public SignatureVerifier() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encodedKey = ByteStreams.toByteArray(SignatureVerifier.class.getResourceAsStream("/signing_pub.key"));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        publicKey = keyFactory.generatePublic(keySpec);
    }

    public boolean verify(String str, String signature) {
        return verify(str.getBytes(StandardCharsets.UTF_8), signature);
    }

    private boolean verify(byte[] data, String signature) {
        try {
            byte[] decodedSignature = Base64.getDecoder().decode(signature);

            Signature sig = Signature.getInstance("SHA512withECDSA");
            sig.initVerify(publicKey);
            sig.update(data);

            return sig.verify(decodedSignature);
        } catch (IllegalArgumentException | SignatureException | NoSuchAlgorithmException | InvalidKeyException e) { // catching for illegal base64 encoding or invalid encoding for signatures
            return false;
        }
    }
}
