package guru.qa.niffler.data.dataClasses;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.users.UserJson;

import java.util.UUID;

public class UserData {

    private static final Config CFG = Config.getInstance();

    public static UserJson mainUser() {
         return new UserJson(
                UUID.fromString("0ab0f2a4-5236-4022-864a-a873b8750e0a"),
                CFG.mainUserLogin(),
                null,
                null,
                null,
                null,
                null,
                null,
                CFG.mainUserPass()
        );
    }
}
