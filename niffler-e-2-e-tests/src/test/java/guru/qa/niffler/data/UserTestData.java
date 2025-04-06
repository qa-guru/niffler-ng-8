package guru.qa.niffler.data;

import com.github.javafaker.Faker;

public class UserTestData {
    public static final Faker faker = new Faker();

    public static String rndUsername() {
        return faker.name().username();
    }

    public static String randomPassword() {
        return faker.internet().password(3, 12);
    }
}
