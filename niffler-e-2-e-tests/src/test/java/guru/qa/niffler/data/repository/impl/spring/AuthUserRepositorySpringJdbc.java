package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.spring.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {
    private static final Config CFG = Config.getInstance();


    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.authJdbcUrl()
    );
    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        return jdbcTxTemplate.execute(() -> {
            authUserDao.create(user);
            for (AuthorityEntity authority : user.getAuthorities()) {
                authAuthorityDao.create(authority);
            }
            return user;
        });
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        return jdbcTxTemplate.execute(() -> {
            authUserDao.update(user);
            if (user.getAuthorities()!=null) {
                authAuthorityDao.update(user.getAuthorities().toArray(new AuthorityEntity[0]));
            }
            return user;
        });
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                    SELECT a.id as authority_id,
                                   authority,
                                   user_id as id,
                                   u.username,
                                   u.password,
                                   u.enabled,
                                   u.account_non_expired,
                                   u.account_non_locked,
                                   u.credentials_non_expired
                                   FROM "user" u join authority a on u.id = a.user_id WHERE u.id = ?
                                """,
                        AuthUserEntityExtractor.instance,
                        id
                )
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                    SELECT a.id as authority_id,
                                   authority,
                                   user_id as id,
                                   u.username,
                                   u.password,
                                   u.enabled,
                                   u.account_non_expired,
                                   u.account_non_locked,
                                   u.credentials_non_expired
                                   FROM "user" u join authority a on u.id = a.user_id WHERE u.username = ?
                                """,
                        AuthUserEntityExtractor.instance,
                        username
                )
        );
    }

    @Override
    public void remove(AuthUserEntity user) {
        jdbcTxTemplate.execute(() -> {
            for (AuthorityEntity authority : user.getAuthorities()) {
                authAuthorityDao.delete(authority);
            }
            authUserDao.delete(user);
            return user;
        });
    }
}