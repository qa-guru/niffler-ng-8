package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryJdbc implements AuthUserRepository {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

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
        return jdbcTxTemplate.execute(() ->
                authUserDao.findById(id)
                .map(user -> {
                    user.addAuthorities(
                            authAuthorityDao
                            .findByUserId(
                                    user
                                            .getId())
                            .toArray(new AuthorityEntity[0])
                    );
                    return user;
                })
        );
    }


    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return jdbcTxTemplate.execute(() ->
                authUserDao.findByUsername(username)
                .map(user -> {
                    user.addAuthorities(
                            authAuthorityDao
                                    .findByUserId(
                                            user
                                                    .getId())
                                    .toArray(new AuthorityEntity[0])
                    );
                    return user;
                })
        );
    }

    @Override
    public void remove(AuthUserEntity user) {
        jdbcTxTemplate.execute(() -> {
            if (user.getAuthorities()!=null) {
                authAuthorityDao.update(user.getAuthorities().toArray(new AuthorityEntity[0]));
            }
            authUserDao.delete(user);
            return user;
        });
    }
}