package guru.qa.niffler.db.repository.impl;

import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.repository.AuthUserRepository;
import guru.qa.niffler.db.repository.JdbcExecutor;
import guru.qa.niffler.db.repository.mapper.AuthAuthorityEntityRowMapper;
import guru.qa.niffler.db.repository.mapper.AuthUserEntityRowMapper;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private final JdbcExecutor<AuthUserEntity> userExec;
    private final JdbcExecutor<AuthAuthorityEntity> authorityExec;

    public AuthUserRepositoryJdbc(String jdbcUrl) {
        this.userExec = new JdbcExecutor<>(jdbcUrl, AuthUserEntityRowMapper.INSTANCE::mapRow);
        this.authorityExec = new JdbcExecutor<>(jdbcUrl, AuthAuthorityEntityRowMapper.INSTANCE::mapRow);
    }

    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        String password = passwordEncoder.encode(user.getPassword());

        String userSql = "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        AuthUserEntity createdUser = userExec.executeQuery(userSql, user.getUsername(), password, user.getEnabled(),
                user.getAccountNonExpired(), user.getAccountNonLocked(), user.getCredentialsNonExpired());

        String authoritySql = "INSERT INTO authority (user_id, authority) " +
                "VALUES(?, ?) RETURNING *";
        for (AuthAuthorityEntity authority : user.getAuthorities()) {
            AuthAuthorityEntity createdAuthority = authorityExec.executeQuery(
                    authoritySql, createdUser.getId(), authority.getAuthority().name()
            );
            createdUser.addAuthorities(createdAuthority);
        }
        return createdUser;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        String userSql = "UPDATE category SET username = ?, password = ?, enabled = ?, " +
                "account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? WHERE id = ? RETURNING *";
        String password = passwordEncoder.encode(user.getPassword());
        AuthUserEntity createdUser = userExec.executeQuery(userSql, user.getUsername(), password, user.getEnabled(),
                user.getAccountNonExpired(), user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getId());

        String authoritySql = "UPDATE authority SET user_id = ?, authority = ? WHERE id = ? RETURNING *";
        for (AuthAuthorityEntity authority : user.getAuthorities()) {
            AuthAuthorityEntity createdAuthority = authorityExec.executeQuery(
                    authoritySql, createdUser.getId(), authority.getAuthority().name(), authority.getId()
            );
            createdUser.addAuthorities(createdAuthority);
        }
        return createdUser;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        Optional<AuthUserEntity> optUser = userExec.executeQueryToOptional(sql, id);
        optUser.ifPresent(this::setAuthorities);
        return optUser;
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        Optional<AuthUserEntity> optUser = userExec.executeQueryToOptional(sql, username);
        optUser.ifPresent(this::setAuthorities);
        return optUser;
    }

    @Override
    public boolean delete(AuthUserEntity user) {
        UUID userId = user.getId();
        String authoritySql = "DELETE FROM authority WHERE user_id = ?";
        authorityExec.executeUpdateToBoolean(authoritySql, userId);

        String userSql = "DELETE FROM \"user\" WHERE id = ?";
        boolean isUserDeleted = userExec.executeUpdateToBoolean(userSql, userId);
        return isUserDeleted;
    }

    @Override
    public List<AuthUserEntity> findAll() {
        String sql = "SELECT * FROM \"user\"";
        List<AuthUserEntity> users = userExec.executeQueryToList(sql);
        for (AuthUserEntity user : users) {
            setAuthorities(user);
        }
        return users;
    }

    private void setAuthorities(AuthUserEntity user) {
        String authoritySql = "SELECT * FROM authority WHERE user_id = ?";
        List<AuthAuthorityEntity> authorities = authorityExec.executeQueryToList(authoritySql, user.getId());
        user.addAuthorities(authorities);
    }

}
