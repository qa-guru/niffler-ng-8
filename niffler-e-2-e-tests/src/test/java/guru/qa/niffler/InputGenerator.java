package guru.qa.niffler;

import java.security.SecureRandom;
import java.util.UUID;

public class InputGenerator {

    private static final String HEX_CHARACTERS = "123457890abcdef";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static UUID generateID() {
        StringBuilder id = new StringBuilder();
        id.append(generateRandomHex(random, 8)).append("-");
        id.append(generateRandomHex(random, 4)).append("-");
        id.append(generateRandomHex(random, 4)).append("-");
        id.append(generateRandomHex(random, 4)).append("-");
        id.append(generateRandomHex(random, 12));
        return UUID.fromString(id.toString());
    }

    private static String generateRandomHex(SecureRandom random, int length) {
        StringBuilder hexPart = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(HEX_CHARACTERS.length());
            hexPart.append(HEX_CHARACTERS.charAt(index));
        }
        return hexPart.toString();
    }

    public static String getRandomString(int countSigns) {
        StringBuilder stringBuilder = new StringBuilder(countSigns);

        for (int i = 0; i < countSigns; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }

        return stringBuilder.toString();
    }
}
