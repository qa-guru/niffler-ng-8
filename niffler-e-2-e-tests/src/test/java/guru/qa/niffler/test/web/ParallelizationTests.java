package guru.qa.niffler.test.web;

import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class ParallelizationTests extends BaseUITest {


    @Order(1)
    @Test
    void emptyTest() {
        Assertions.assertTrue(userClient.allUsers(RandomDataUtils.randomName(), null).isEmpty());
    }

    @Order(Integer.MAX_VALUE)
    @Test
    void notEmptyTest() {
        Assertions.assertFalse(userClient.allUsers(RandomDataUtils.randomName(), null).isEmpty());
    }
}
