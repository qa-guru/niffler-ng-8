package utils;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FakerGenUtil {

    private final Faker faker = new Faker();

    public String genRandomName() {
        return faker.name().fullName();
    }

    public String genRandomEmail() {
        return faker.internet().emailAddress();
    }

    public  String genRandomPassword() {
        return faker.internet().password(3, 10);
    }
}
