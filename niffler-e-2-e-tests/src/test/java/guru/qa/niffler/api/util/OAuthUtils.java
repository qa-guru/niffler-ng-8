package guru.qa.niffler.api.util;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class OAuthUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateCodeVerifier() {
        byte[] code = new byte[32];
        secureRandom.nextBytes(code);
        return base64UrlEncoder.encodeToString(code);
    }

    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncoder.encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate code_challenge", e);
        }
    }
}
