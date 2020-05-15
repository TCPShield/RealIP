package net.tcpshield.tcpshield;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SignatureObjectVerifierTest {

    private static SignatureVerifier signatureVerifier;

    @BeforeAll
    public static void setUp() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        signatureVerifier = new SignatureVerifier();
    }

    @Test
    public void testVerificationSuccess() {
        SignatureObject signatureObject = createSignature(TestConstants.VALID_PAYLOAD_INVALID_TIMESTAMP);
        assertTrue(signatureVerifier.verify(signatureObject.getReconstructedPayload(), signatureObject.getSignature()));
    }

    @Test
    public void testVerificationFailure() {
        SignatureObject signatureObject = createSignature(TestConstants.INVALID_PAYLOAD_INVALID_SIGNATURE);
        assertFalse(signatureVerifier.verify(signatureObject.getReconstructedPayload(), signatureObject.getSignature()));
    }

    private SignatureObject createSignature(String payload) {
        int signatureIndex = payload.lastIndexOf("///");
        String signature = payload.substring(signatureIndex + 3);
        String reconstructedPayload = payload.substring(0, signatureIndex);

        return new SignatureObject(signature, reconstructedPayload);
    }

    private static class SignatureObject {

        private final String signature;
        private final String reconstructedPayload;

        private SignatureObject(String signature, String reconstructedPayload) {
            this.signature = signature;
            this.reconstructedPayload = reconstructedPayload;
        }

        public String getSignature() {
            return signature;
        }

        public String getReconstructedPayload() {
            return reconstructedPayload;
        }
    }
}
