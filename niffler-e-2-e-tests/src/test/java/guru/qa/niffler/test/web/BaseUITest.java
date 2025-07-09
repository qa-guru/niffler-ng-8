package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.AllPages;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersClient;

public class BaseUITest extends AllPages {
    protected static final Config CFG = Config.getInstance();

    protected String monkeyPngPath = "C:\\niffler-ng-81\\niffler-e-2-e-tests\\src\\test\\resources\\img\\bibizyan.jpg";
    protected String kiwiPngPath = "C:\\niffler-ng-81\\niffler-e-2-e-tests\\src\\test\\resources\\img\\Kiwi.jpg";

    UsersClient userClient = UsersClient.getInstance();
    SpendDbClient spendDbClient = new SpendDbClient();
}
