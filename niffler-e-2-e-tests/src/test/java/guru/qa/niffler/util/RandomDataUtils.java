package guru.qa.niffler.util;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.model.Authority;
import guru.qa.niffler.api.model.AuthorityJson;
import guru.qa.niffler.api.model.UserJson;

import java.util.List;

import static guru.qa.niffler.api.model.CurrencyValues.RUB;

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

    public static UserJson genDefaultUser() {
        UserJson userJson = new UserJson();
        List<AuthorityJson> defaultAuthorities = List.of(
                new AuthorityJson().setAuthority(Authority.write),
                new AuthorityJson().setAuthority(Authority.write)
        );
        return userJson
                .setUsername(genUsername())
                .setPassword(genPassword())
                .setEnabled(true)
                .setAccountNonExpired(true)
                .setAccountNonLocked(true)
                .setCredentialsNonExpired(true)
                .setAuthorities(defaultAuthorities)
                .setCurrency(RUB);
    }

}
