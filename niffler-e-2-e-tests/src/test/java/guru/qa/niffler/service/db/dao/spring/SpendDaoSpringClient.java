package guru.qa.niffler.service.db.dao.spring;

import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.service.db.dao.SpendDaoDbClient;

public class SpendDaoSpringClient extends SpendDaoDbClient {
    public SpendDaoSpringClient(){
        super(
                new CategoryDaoSpringJdbc(),
                new SpendDaoSpringJdbc()
        );
    }
}
