package guru.qa.niffler;

import com.github.javafaker.Faker;

public class DataUtils {

    private Faker faker = new Faker();

    public String generateCategoryName() {
        return faker.commerce().department();
    }

    public String generateUserName() {
        return faker.name().username();
    }

    public String generateUserPassword() {
        return faker.internet().password(3, 6);
    }

}
