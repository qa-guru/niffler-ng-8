package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;

public class RandomDataUtils {

    public static final Faker faker = new Faker();

    @Nonnull
    public static String randomCategoryName() {
        return "Category_" + faker.random().nextInt(100000000, 999999999);
    }

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomSurname() {
        return faker.name().lastName();
    }

    @Nonnull
    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }

    @Nonnull
    public static String randomPassword() {
        return faker.internet().password(3, 12);
    }

}
