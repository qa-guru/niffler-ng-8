package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final JdbcTemplate jdbcTemplate =
            new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        UUID userId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO auth.auth_user (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                userId,
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired()
        );
        user.setId(userId);
        return user;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity authUser) {
        jdbcTemplate.update(
                "UPDATE auth.auth_user SET password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? WHERE id = ?",
                authUser.getPassword(),
                authUser.getEnabled(),
                authUser.getAccountNonExpired(),
                authUser.getAccountNonLocked(),
                authUser.getCredentialsNonExpired(),
                authUser.getId()
        );
        return authUser;
    }

    @Override
    public Optional<AuthUserEntity> findById(String id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM auth.auth_user WHERE id = ?",
                            new Object[]{UUID.fromString(id)},
                            new AuthUserEntityRowMapper()
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM auth.auth_user WHERE username = ?",
                            new Object[]{username},
                            new AuthUserEntityRowMapper()
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity authUser) {
        jdbcTemplate.update("DELETE FROM auth.auth_user WHERE id = ?", authUser.getId());

    }
}
