package net.tcpshield.tcpshield.util.validation;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * A signature validator using the TCPShield public key
 */
public class SignatureValidator {

	private final PublicKey publicKey;

	public SignatureValidator() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedKey = ByteStreams.toByteArray(SignatureValidator.class.getResourceAsStream("/signing_pub.key"));
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);

		KeyFactory keyFactory = KeyFactory.getInstance("EC");
		publicKey = keyFactory.generatePublic(keySpec);
	}


	/**
	 * Validates a String and Signature pair
	 * @param str The data in the form of a string
	 * @param signature The provided signature
	 * @return Boolean stating if it's a valid signature
	 */
	public boolean validate(String str, String signature) {
		return validate(str.getBytes(StandardCharsets.UTF_8), signature);
	}

	/**
	 * Validates a byte[] and Signature pair
	 * @param data The data in the form of a byte array
	 * @param signature The provided signature
	 * @return Boolean stating if it's a valid signature
	 */
	private boolean validate(byte[] data, String signature) {
		try {
			byte[] decodedSignature = Base64.getDecoder().decode(signature);

			Signature sig = Signature.getInstance("SHA512withECDSA");
			sig.initVerify(publicKey);
			sig.update(data);

			return sig.verify(decodedSignature);
		} catch (IllegalArgumentException | SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
			return false;
		}
	}

}
