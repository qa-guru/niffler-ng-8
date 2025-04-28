package guru.qa.niffler.service.db.dao.jdbc;

import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.service.db.dao.SpendDaoDbClient;

public class SpendDaoJdbcClient extends SpendDaoDbClient {

    public SpendDaoJdbcClient(){
        super(
                new CategoryDaoJdbc(),
                new SpendDaoJdbc()
        );
    }
}
