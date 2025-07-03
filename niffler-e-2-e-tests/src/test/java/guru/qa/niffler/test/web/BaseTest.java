package guru.qa.niffler.test.web;

import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;

public class BaseTest {
    protected UserDbClient userDbClient = new UserDbClient();
    protected SpendDbClient spendDbClient = new SpendDbClient();

    protected String kiwiPngPath = "niffler-e-2-e-tests\\src\\test\\resources\\img\\Kiwi.jpg";
    protected String monkeyPngPath  = "niffler-e-2-e-tests\\src\\test\\resources\\img\\bibizyan.jpg";
}
