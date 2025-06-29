package guru.qa.niffler.db.repository.impl.hibernate;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.repository.AuthUserRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate extends AbstractRepositoryHibernate implements AuthUserRepository {

    private static final Class<AuthUserEntity> USER_CLASS = AuthUserEntity.class;
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthUserRepositoryHibernate() {
        super(CFG.authJdbcUrl());
    }

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity entity) {
        String encodedPass = passwordEncoder.encode(entity.getPassword());
        entity.setPassword(encodedPass);
        return super.create(entity);
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity entity) {
        return super.update(entity);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        return findByIdOpt(USER_CLASS, id);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(String username) {
        String sql = "SELECT u FROM AuthUserEntity u WHERE u.username = ?1";
        return findSingleResultOpt(USER_CLASS, sql, username);
    }

    @Override
    public boolean delete(AuthUserEntity entity) {
        return delete(USER_CLASS, entity.getId());
    }

    @Override
    public @Nullable List<AuthUserEntity> findAll() {
        String sql = "SELECT u FROM AuthUserEntity u";
        return findResultList(USER_CLASS, sql);
    }

}
