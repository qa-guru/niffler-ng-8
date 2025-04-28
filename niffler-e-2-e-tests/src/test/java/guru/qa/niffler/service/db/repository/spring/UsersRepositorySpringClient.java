package guru.qa.niffler.service.db.repository.spring;

import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserDataRepositorySpringJdbc;
import guru.qa.niffler.service.db.repository.UserRepositoryDbClient;

public class UsersRepositorySpringClient extends UserRepositoryDbClient {

    public UsersRepositorySpringClient() {
        super(new AuthUserRepositorySpringJdbc(), new UserDataRepositorySpringJdbc());
    }
}