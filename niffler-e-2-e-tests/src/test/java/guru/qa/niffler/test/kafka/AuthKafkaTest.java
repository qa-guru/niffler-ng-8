package guru.qa.niffler.test.kafka;

import guru.qa.niffler.jupiter.annotation.meta.KafkaTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.KafkaService;
import guru.qa.niffler.service.api.UsersApiClient;
import guru.qa.niffler.service.db.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@KafkaTest
public class AuthKafkaTest {

  private final UsersApiClient usersApiClient = new UsersApiClient();
  private final UsersDbClient usersDbClient = new UsersDbClient();

  @Test
  void userShouldBeProducedToKafka() throws Exception {
    final String username = RandomDataUtils.randomUsername();
    final String password = "12345";

    usersApiClient.createUser(username, password);

    UserJson userFromKafka = KafkaService.getUser(username);
    Assertions.assertEquals(
        username,
        userFromKafka.username()
    );
  }

  @Test
  void whenUserProducedToKafkaThenUserAddedToDb() throws Exception {
    final String username = RandomDataUtils.randomUsername();
    final String password = "12345";

    usersApiClient.createUser(username, password);

    assertTrue(KafkaService.isProduced(username));

    UserJson userFromDb = usersDbClient.getUser(username);

    Assertions.assertNotNull(userFromDb,"user not found in db: "+username);
  }
}