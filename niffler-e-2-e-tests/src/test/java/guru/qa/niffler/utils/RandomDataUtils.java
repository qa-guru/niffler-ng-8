package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import java.security.SecureRandom;
import java.util.UUID;

public class RandomDataUtils {

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

    public static String randomUserName() {
        return new Faker().name().username();
    }

    public static String randomName() {
        return new Faker().cat().name();
    }

    public static String surnameName() {
        return new Faker().name().lastName();
    }

    public static String categoryName() {
        Faker faker = new Faker();
        return faker.food().ingredient();
    }

    public static String randomSentence(int wordsCount) {
        Faker faker = new Faker();
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < wordsCount; i++) {
            sentence.append(faker.lorem().word()).append(" ");
        }
        return sentence.toString().trim() + ".";
    }
}
