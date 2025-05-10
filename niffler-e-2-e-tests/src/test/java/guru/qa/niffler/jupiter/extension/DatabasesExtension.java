package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.jpa.EntityManagers;
import guru.qa.niffler.db.service.UserClient;
import guru.qa.niffler.db.service.impl.UserDbClient;
import guru.qa.niffler.db.tpl.Connections;
import guru.qa.niffler.db.tpl.XaTransactionTemplate;

public class DatabasesExtension implements SuiteExtension {

    private final UserClient userClient = new UserDbClient();

    @Override
    public void afterSuite() {
        userClient.deleteAllGenUser();
        Connections.closeAllConnections();
        XaTransactionTemplate.closeTransactionManager();
        EntityManagers.closeAllEmfs();
    }

}