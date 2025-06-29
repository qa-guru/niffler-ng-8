package guru.qa.niffler.db.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final String JDBC_URL = Config.getInstance().authJdbcUrl();
    private final AuthUserDao userDao = new AuthUserDaoJdbc(JDBC_URL);
    private final AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc(JDBC_URL);

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity entity) {
        AuthUserEntity createdUser = userDao.create(entity);

        for (AuthAuthorityEntity authority : entity.getAuthorities()) {
            authority.setUser(createdUser);
            AuthAuthorityEntity createdAuthority = authorityDao.create(authority);
            createdUser.addAuthorities(createdAuthority);
        }
        return createdUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity entity) {
        AuthUserEntity updatedUser = userDao.update(entity);

        for (AuthAuthorityEntity authority : entity.getAuthorities()) {
            AuthAuthorityEntity updatedAuthority = authorityDao.update(authority);
            updatedUser.addAuthorities(updatedAuthority);
        }
        return updatedUser;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        return userDao.findById(id)
            .map(this::setAuthorities);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(String username) {
        return userDao.findByUsername(username)
            .map(this::setAuthorities);
    }

    @Override
    public boolean delete(AuthUserEntity entity) {
        return findById(entity.getId())
            .map(u -> {
                authorityDao.findByUserId(u.getId())
                    .forEach(authorityDao::delete);
                return userDao.delete(u);
            })
            .orElse(false);
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        List<AuthUserEntity> users = userDao.findAll();
        for (AuthUserEntity user : users) {
            setAuthorities(user);
        }
        return users;
    }

    private @Nonnull AuthUserEntity setAuthorities(AuthUserEntity entity) {
        List<AuthAuthorityEntity> authorities = authorityDao.findByUserId(entity.getId());
        entity.addAuthorities(authorities);
        return entity;
    }

}
