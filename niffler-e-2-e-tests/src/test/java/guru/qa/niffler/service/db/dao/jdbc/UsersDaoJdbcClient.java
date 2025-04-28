package guru.qa.niffler.service.db.dao.jdbc;

import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.service.db.dao.UsersDaoDbClient;

public class UsersDaoJdbcClient extends UsersDaoDbClient {

    public UsersDaoJdbcClient(){
        super(
                new AuthUserDaoJdbc(),
                new AuthAuthorityDaoJdbc(),
                new UdUserDaoJdbc()
        );
    }
}
