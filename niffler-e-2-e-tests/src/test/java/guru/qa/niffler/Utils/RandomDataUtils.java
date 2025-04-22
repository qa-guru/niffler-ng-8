package guru.qa.niffler.Utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static String randomCategoryName() {
        return faker.commerce().department();
    }

    public static String randomUserName() {
        return faker.name().username();
    }

    public static String randomName() {
        return faker.name().name();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static String randomUserPassword() {
        return faker.internet().password(3, 6);
    }

    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }
}
