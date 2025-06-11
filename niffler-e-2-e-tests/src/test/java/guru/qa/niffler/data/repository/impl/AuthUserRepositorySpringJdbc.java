package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.extractor.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();

  private final String url = CFG.authJdbcUrl();
  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    authUserDao.create(user);
    authAuthorityDao.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
    return user;
  }
    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        AuthUserEntity updatedUser = authUserDao.update(user);

        List<AuthorityEntity> existingAuthorities = authAuthorityDao.findByUserId(user.getId());
        existingAuthorities.forEach(authAuthorityDao::delete);

        if (!user.getAuthorities().isEmpty()) {
            user.getAuthorities().forEach(a -> a.setUser(updatedUser));
            authAuthorityDao.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
        }
        return updatedUser;
    }

    @NotNull
    @Nonnull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            "SELECT a.id AS authority_id, a.authority, a.user_id, " +
                                    "u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                                    "FROM \"user\" u JOIN public.authority a ON u.id = a.user_id " +
                                    "WHERE u.id = ?",
                            AuthUserEntityResultSetExtractor.instance,
                            id
                    )
            );
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        Optional<AuthUserEntity> user = authUserDao.findByUsername(username);
        user.ifPresent(u ->
                u.setAuthorities(authAuthorityDao.findByUserId(u.getId()))
        );
        return user;
    }

    @Override
    public void remove(AuthUserEntity user) {
        List<AuthorityEntity> existingAuthorities = authAuthorityDao.findByUserId(user.getId());
        existingAuthorities.forEach(authAuthorityDao::delete);
        authUserDao.delete(user);
    }
}
