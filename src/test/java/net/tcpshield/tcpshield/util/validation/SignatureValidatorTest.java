package net.tcpshield.tcpshield.util.validation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SignatureValidatorTest {

	/*
	 * A P-384 key generated for these tests, and a SHA512withECDSA signature of PAYLOAD made with it
	 */
	private static final String PUBLIC_KEY = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEIZ9esG5Czc34ca9Jh3SzyCPb3psMqTWzQpYipgvHMtvRW0NN3gICA7mYEafeDym7T5LDrSCJIByFhNiMF6i9Qo5Y3B/7MliWh4/7dLBxk1HNlUB4j31N8l/b32Gkmxg8";
	private static final String PAYLOAD = "example.com///203.0.113.7:25565///1700000000";
	private static final String SIGNATURE = "MGUCMQDh1lCBfLdMxcMqwI/WKbFSo9w8N0KP+KflwRWxY8+fXJKqNL2546X5zFyEldhdTasCMD4XQjviTLdzPOAlkxCQKXAP4SeSZWhLEVtbMQI514tbj27YLnSKTRyTeWi7GT9Log==";

	private static SignatureValidator testKeyValidator() throws GeneralSecurityException {
		PublicKey publicKey = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY)));
		return new SignatureValidator(publicKey);
	}

	@Test
	public void acceptsValidSignature() throws GeneralSecurityException {
		assertTrue(testKeyValidator().validate(PAYLOAD, SIGNATURE));
	}

	@Test
	public void rejectsSignatureOfDifferentData() throws GeneralSecurityException {
		SignatureValidator validator = testKeyValidator();

		assertFalse(validator.validate("example.com///203.0.113.8:25565///1700000000", SIGNATURE));
		assertFalse(validator.validate("example.com///203.0.113.7:25565///1700000001", SIGNATURE));
		assertFalse(validator.validate("example.org///203.0.113.7:25565///1700000000", SIGNATURE));
		assertFalse(validator.validate("", SIGNATURE));
	}

	@Test
	public void rejectsAlteredSignature() throws GeneralSecurityException {
		SignatureValidator validator = testKeyValidator();

		byte[] altered = Base64.getDecoder().decode(SIGNATURE);
		altered[altered.length - 1] ^= 1;
		assertFalse(validator.validate(PAYLOAD, Base64.getEncoder().encodeToString(altered)));

		byte[] truncated = Base64.getDecoder().decode(SIGNATURE);
		assertFalse(validator.validate(PAYLOAD, Base64.getEncoder().encodeToString(java.util.Arrays.copyOf(truncated, truncated.length - 4))));
	}

	@Test
	public void rejectsMalformedSignaturesWithoutThrowing() throws GeneralSecurityException {
		SignatureValidator validator = testKeyValidator();

		assertFalse(validator.validate(PAYLOAD, ""));
		assertFalse(validator.validate(PAYLOAD, "not base64!"));
		assertFalse(validator.validate(PAYLOAD, "AAAA"));
		assertFalse(validator.validate(PAYLOAD, SIGNATURE.substring(0, SIGNATURE.length() - 1)));

		Random random = new Random(1);
		for (int i = 0; i < 1000; i++) {
			byte[] garbage = new byte[random.nextInt(200)];
			random.nextBytes(garbage);
			assertFalse(validator.validate(PAYLOAD, Base64.getEncoder().encodeToString(garbage)));
		}
	}

	@Test
	public void bundledKeyRejectsSignatureOfAnotherKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		assertFalse(new SignatureValidator().validate(PAYLOAD, SIGNATURE));
	}

}
