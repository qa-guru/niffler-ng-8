package guru.qa.niffler.util;

import com.github.javafaker.Faker;

public final class GenerationUtil {

    private static final Faker FAKER = Faker.instance();

    private GenerationUtil() {
        throw new UnsupportedOperationException();
    }

    public static String genPassword() {
        return FAKER.internet().password(3, 12);
    }

    public static String genUsername() {
        return FAKER.funnyName().name();
    }

}
