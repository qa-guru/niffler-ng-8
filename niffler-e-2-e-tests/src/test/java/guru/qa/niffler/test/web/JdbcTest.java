package guru.qa.niffler.test.web;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@Disabled
public class JdbcTest {

  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "cat-name-tx-2",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-tx",
            "dima"
        )
    );

    System.out.println(spend);
  }

  @Test
  void xaTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUser(
        new UserJson(
            null,
            "valentin-4",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void springJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserSpringJdbc(
        new UserJson(
            null,
            "valentin-6",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void findAllWithAuthoritiesTest() {
    AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    List<AuthUserEntity> usersWithAuthorities = authUserDao.findAllWithAuthorities();

    assertNotNull(usersWithAuthorities);
    assertFalse(usersWithAuthorities.isEmpty());

    for (AuthUserEntity user : usersWithAuthorities) {
      System.out.printf("User: %s (%s)%n", user.getUsername(), user.getId());
      if (user.getAuthorities() != null) {
        for (AuthorityEntity authority : user.getAuthorities()) {
          System.out.printf("  Authority: %s%n", authority.getAuthority());
        }
      } else {
        System.out.println("  No authorities");
      }
    }
  }
}
