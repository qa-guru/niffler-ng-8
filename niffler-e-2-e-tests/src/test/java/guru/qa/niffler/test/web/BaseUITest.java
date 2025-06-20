package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.AllPages;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.service.UsersClient;

public class BaseUITest extends AllPages {
    protected static final Config CFG = Config.getInstance();

    protected String monkeyPngPath = "resources/img/bibizyan.jpg";
    protected String kiwiPngPath = "resources/img/Kiwi.jpg";

    UsersClient userDbClient = UsersClient.getInstance();
    SpendDbClient spendDbClient = new SpendDbClient();
}
