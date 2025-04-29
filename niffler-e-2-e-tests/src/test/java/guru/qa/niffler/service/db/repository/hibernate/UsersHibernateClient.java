package guru.qa.niffler.service.db.repository.hibernate;

import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.service.db.repository.UserRepositoryDbClient;

public class UsersHibernateClient extends UserRepositoryDbClient {

    public UsersHibernateClient() {
        super(new AuthUserRepositoryHibernate(), new UserdataUserRepositoryHibernate());
    }
}
