package guru.qa.niffler.jupiter.databases;

import guru.qa.niffler.data.DataBases;
import guru.qa.niffler.jupiter.SuiteExtension;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite(){
        DataBases.closeAllConnections();
    }
}