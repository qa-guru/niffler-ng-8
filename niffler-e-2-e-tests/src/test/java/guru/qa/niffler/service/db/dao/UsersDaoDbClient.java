package guru.qa.niffler.service.db.dao;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.db.UsersDbClient;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public abstract class UsersDaoDbClient extends UsersDbClient {
    protected final AuthUserDao authUserDao;
    protected final AuthAuthorityDao authorityUserDao;
    protected final UdUserDao udUserDao;

    protected UsersDaoDbClient(
            AuthUserDao authUserDao,
            AuthAuthorityDao authorityUserDao,
            UdUserDao udUserDao){
        this.authUserDao = authUserDao;
        this.authorityUserDao = authorityUserDao;
        this.udUserDao = udUserDao;
    }

    public UserJson notXaCreateUser(String username, String password) {
        AuthUserEntity authUserEntity = authUserEntity(username,password);
        authUserDao.create(authUserEntity);

        authorityUserDao.create(authUserEntity.getAuthorities().toArray(new AuthorityEntity[0]));
        return UserJson.fromEntity(
                udUserDao.create(userEntity(username)
                ));
    }

    @Override
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(username,password);
            authUserDao.create(authUserEntity);

            authorityUserDao.create(authUserEntity.getAuthorities().toArray(new AuthorityEntity[0]));
            return UserJson.fromEntity(
                    udUserDao.create(userEntity(username)
                    ));
        });
    }



    private final TransactionTemplate xaTransactionTemplateChained = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            dataSource(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            dataSource(CFG.userdataJdbcUrl())
                    )
            )
    );

    public UserJson createUserChainedTxManager(String username, String password) {
        return xaTransactionTemplateChained.execute(status -> {
            AuthUserEntity authUserEntity = authUserEntity(username,password);
            authUserDao.create(authUserEntity);

            authorityUserDao.create(authUserEntity.getAuthorities().toArray(new AuthorityEntity[0]));
            return UserJson.fromEntity(
                    udUserDao.create(userEntity(username)
                    ));
        });
    }
}
