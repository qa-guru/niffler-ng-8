package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.db.CategoryDbClient;
import guru.qa.niffler.db.SpendDbClient;
import guru.qa.niffler.model.CategoryJson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DbTests {
    private final SpendDbClient spendDbClient = new SpendDbClient();
    private final CategoryDbClient categoryDbClient = new CategoryDbClient();

    @Test
    void dbTest() {
        CategoryEntity category = categoryDbClient.create(
                new CategoryJson(
                        null,
                        "name-test1",
                        "user-name-test1",
                        false
                )
        );
        log.info(category.toString());
    }
}
