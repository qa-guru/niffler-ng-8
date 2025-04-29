package guru.qa.niffler.jupiter.databases;

import guru.qa.niffler.data.tpl.Connections;
import guru.qa.niffler.jupiter.SuiteExtension;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}