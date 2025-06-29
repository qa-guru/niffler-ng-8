package guru.qa.niffler.db.repository.impl.spring_jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.db.dao.impl.spring_jdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final String JDBC_URL = Config.getInstance().authJdbcUrl();
    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc(JDBC_URL);
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc(JDBC_URL);

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity entity) {
        AuthUserEntity createdUser = authUserDao.create(entity);

        for (AuthAuthorityEntity authority : entity.getAuthorities()) {
            authority.setUser(createdUser);
            AuthAuthorityEntity createdAuthority = authAuthorityDao.create(authority);
            createdUser.addAuthorities(createdAuthority);
        }
        return createdUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity entity) {
        AuthUserEntity updatedUser = authUserDao.update(entity);

        for (AuthAuthorityEntity authority : entity.getAuthorities()) {
            AuthAuthorityEntity updatedAuthority = authAuthorityDao.update(authority);
            updatedUser.addAuthorities(updatedAuthority);
        }
        return updatedUser;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        return authUserDao.findById(id)
            .map(this::setAuthorities);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(String username) {
        return authUserDao.findByUsername(username)
            .map(this::setAuthorities);
    }

    @Override
    public boolean delete(AuthUserEntity entity) {
        return findById(entity.getId())
            .map(u -> {
                authAuthorityDao.findByUserId(u.getId())
                    .forEach(authAuthorityDao::delete);
                return authUserDao.delete(u);
            })
            .orElse(false);
    }

    @Override
    public @Nullable List<AuthUserEntity> findAll() {
        List<AuthUserEntity> users = authUserDao.findAll();
        for (AuthUserEntity user : users) {
            setAuthorities(user);
        }
        return users;
    }

    private @Nonnull AuthUserEntity setAuthorities(AuthUserEntity entity) {
        List<AuthAuthorityEntity> authorities = authAuthorityDao.findByUserId(entity.getId());
        entity.addAuthorities(authorities);
        return entity;
    }

}
