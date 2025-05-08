package guru.qa.niffler.db.service;

import guru.qa.niffler.api.model.AuthUserJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.UserdataUserDaoJdbc;
import guru.qa.niffler.db.dao.impl.spring_jdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.db.dao.impl.spring_jdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.db.dao.impl.spring_jdbc.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.tpl.DataSources;
import guru.qa.niffler.db.tpl.XaTransactionTemplate;
import lombok.Getter;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDbExperimentalService extends AbstractDbClient {

    private final String AUTH_DB_URL = CFG.authJdbcUrl();
    private final String USERDATA_DB_URL = CFG.userdataJdbcUrl();
    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public static DataSource simpleDataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                url -> {
                    PGSimpleDataSource ds = new PGSimpleDataSource();
                    ds.setUser("postgres");
                    ds.setPassword("secret");
                    ds.setUrl(url);
                    return ds;
                }
        );
    }


    @Getter
    private final XaTransactionTemplate xaTxTemplateWithAtomikosDs = new XaTransactionTemplate(AUTH_DB_URL, USERDATA_DB_URL);

    @Getter
    private final TransactionTemplate chainedTxTemplateWithSimpleDs = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(simpleDataSource(AUTH_DB_URL)),
                    new JdbcTransactionManager(simpleDataSource(USERDATA_DB_URL))
            )
    );

    @Getter
    private final Client springJdbcClientWithAtomikosDs = new Client(
            new AuthUserDaoSpringJdbc(DataSources.dataSource(AUTH_DB_URL)),
            new AuthAuthorityDaoSpringJdbc(DataSources.dataSource(AUTH_DB_URL)),
            new UserdataUserDaoSpringJdbc(DataSources.dataSource(USERDATA_DB_URL))
    );

    @Getter
    private final Client springJdbcClientWithSimpleDs = new Client(
            new AuthUserDaoSpringJdbc(simpleDataSource(AUTH_DB_URL)),
            new AuthAuthorityDaoSpringJdbc(simpleDataSource(AUTH_DB_URL)),
            new UserdataUserDaoSpringJdbc(simpleDataSource(USERDATA_DB_URL))
    );

    @Getter
    private final Client jbcClientWithWithAtomikosDs = new Client(
            new AuthUserDaoJdbc(AUTH_DB_URL),
            new AuthAuthorityDaoJdbc(AUTH_DB_URL),
            new UserdataUserDaoJdbc(USERDATA_DB_URL)
    );

    public static class Client {

        private final AuthUserDao authUserDao;
        private final AuthAuthorityDao authAuthorityDao;
        private final UserdataUserDao userdataUserDao;

        public Client(AuthUserDao authUserDao, AuthAuthorityDao authAuthorityDao, UserdataUserDao userdataUserDao) {
            this.authUserDao = authUserDao;
            this.authAuthorityDao = authAuthorityDao;
            this.userdataUserDao = userdataUserDao;
        }

        public UserParts createUser(UserParts userJson) {
            UserdataUserEntity userdataUser = userJson.getUserdataUserEntity();
            AuthUserEntity authUser = userJson.getAuthUserEntity();
            authUser = authUserDao.create(authUser);
            for (AuthAuthorityEntity authority : authUser.getAuthorities()) {
                authority.setUser(authUser);
                AuthAuthorityEntity authAuthority = authAuthorityDao.create(authority);
                authUser.addAuthorities(authAuthority);
            }
            userdataUser = userdataUserDao.create(userdataUser);
            UserdataUserJson userdataUserJson = UserdataUserJson.fromEntity(userdataUser);
            AuthUserJson authUserJson = AuthUserJson.fromEntity(authUser);
            return new UserParts(authUserJson, userdataUserJson);
        }

        public void deleteUser(UserParts userJson) {
            deleteAuthUserAndAuthority(userJson.getAuthUserJson());
            deleteUserdataUser(userJson.getUserdataUserJson());
        }

        private void deleteUserdataUser(UserdataUserJson userJson) {
            UserdataUserEntity userdataUser;
            if (userJson.getId() != null) {
                userdataUser = UserdataUserEntity.fromJson(userJson);
            } else if (userJson.getUsername() != null) {
                userdataUser = userdataUserDao.findByUsername(userJson.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                        ));
            } else {
                throw new IllegalArgumentException("id и username == null");
            }
            userdataUserDao.delete(userdataUser);
        }

        private void deleteAuthUserAndAuthority(AuthUserJson userJson) {
            AuthUserEntity authUser;
            if (userJson.getId() != null) {
                authUser = AuthUserEntity.fromJson(userJson);
            } else if (userJson.getUsername() != null) {
                authUser = authUserDao.findByUsername(userJson.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username"
                        ));
                List<AuthAuthorityEntity> authorities = authAuthorityDao.findByUserId(authUser.getId());
                authUser.addAuthorities(authorities);
            } else {
                throw new IllegalArgumentException("id и username == null");
            }
            for (AuthAuthorityEntity authority : authUser.getAuthorities()) {
                authAuthorityDao.delete(authority);
            }
            authUserDao.delete(authUser);
        }
    }

}
