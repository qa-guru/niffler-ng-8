package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.tpl.Connections;
import guru.qa.niffler.db.tpl.XaTransactionTemplate;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
        XaTransactionTemplate.closeTransactionManager();
    }

}