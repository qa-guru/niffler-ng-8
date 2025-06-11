package guru.qa.niffler.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class OauthUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();
    private static final int CODE_VERIFIER_LENGTH = 32;

    public static String generateCodeVerifier() {
        byte[] codeVerifierBytes = new byte[CODE_VERIFIER_LENGTH];
        secureRandom.nextBytes(codeVerifierBytes);
        return base64UrlEncoder.encodeToString(codeVerifierBytes);
    }

    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return base64UrlEncoder.encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate code challenge from code verifier", e);
        }
    }
}
