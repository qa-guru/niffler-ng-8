package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.data.tpl.Connections;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}