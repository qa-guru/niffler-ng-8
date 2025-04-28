package guru.qa.niffler.service.db.repository.jdbc;

import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserDataRepositoryJdbc;
import guru.qa.niffler.service.db.repository.UserRepositoryDbClient;

public class UsersRepositoryJdbcClient extends UserRepositoryDbClient {

    public UsersRepositoryJdbcClient() {
        super(new AuthUserRepositoryJdbc(), new UserDataRepositoryJdbc());
    }
}
