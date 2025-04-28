package guru.qa.niffler.service.db.dao.spring;

import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoSpringJdbc;
import guru.qa.niffler.service.db.dao.UsersDaoDbClient;

public class UsersDaoSpringClient  extends UsersDaoDbClient {
    public UsersDaoSpringClient(){
        super(
                new AuthUserDaoSpringJdbc(),
                new AuthAuthorityDaoSpringJdbc(),
                new UserDaoSpringJdbc()
        );
    }
}
