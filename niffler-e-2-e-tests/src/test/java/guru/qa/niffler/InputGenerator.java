package guru.qa.niffler;

import java.security.SecureRandom;
import java.util.UUID;

public class InputGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static UUID generateID() {
        return UUID.randomUUID();
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
