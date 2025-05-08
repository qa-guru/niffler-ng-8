package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import java.util.Random;

public class RandomDataUtils {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static String randomUsername(){
        return faker.name().username();
    }
    public static String randomName(){
        return faker.name().fullName();
    }

    public static String randomEmail(){
        return faker.name().fullName();
    }

    public static String randomPassword(int minLength, int maxLength){
        return faker.internet().password(minLength, maxLength);
    }

    public static String randomString(int length){
        return faker.lorem().characters(length);
    }

    public static String randomSurname(){
        return faker.name().lastName();
    }
    public static String randomCategoryName(){
        return faker.commerce().department();
    }

    public static String randomSpendingDescription(){
        return randomSentence(2);
    }

    public static double randomSpendingAmount(){
        return 50 + random.nextInt(50000 - 50 + 1);
    }

    public static String randomSentence(int wordsCount){
        StringBuilder sentence = new StringBuilder();

        for (int i = 0; i < wordsCount; i++) {
            sentence.append(faker.lorem().word()).append(" ");
        }

        return sentence.toString().trim();
    }
}
