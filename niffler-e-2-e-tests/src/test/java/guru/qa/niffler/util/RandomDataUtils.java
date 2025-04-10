package guru.qa.niffler.util;

import com.github.javafaker.Faker;

public final class RandomDataUtils {

    private static final Faker FAKER = Faker.instance();

    private RandomDataUtils() {
        throw new UnsupportedOperationException();
    }

    public static String genPassword() {
        return FAKER.internet().password(3, 12);
    }

    public static String genUsername() {
        return FAKER.funnyName().name();
    }

    public static String genCategoryName() {
        return FAKER.app().name() + Thread.currentThread().threadId();
    }

}
