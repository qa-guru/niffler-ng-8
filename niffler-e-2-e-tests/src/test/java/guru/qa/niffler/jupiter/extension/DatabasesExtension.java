package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.Databases;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Databases.closeAllConnection();
        Databases.closeTransactionManager();
    }

}