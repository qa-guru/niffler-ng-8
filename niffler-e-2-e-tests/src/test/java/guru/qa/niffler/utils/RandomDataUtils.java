package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {

    public static final Faker faker = new Faker();

    public static String randomCategoryName() {
        return "Category_" + faker.random().nextInt(100000000, 999999999);
    }

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }

    public static String randomPassword() {
        return faker.internet().password(3, 12);
    }

}
