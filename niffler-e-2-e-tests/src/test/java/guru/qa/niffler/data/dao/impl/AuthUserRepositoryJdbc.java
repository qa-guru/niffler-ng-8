package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String DB_URL = CFG.authJdbcUrl();

    private final JdbcTemplate jdbcTemplate =
            new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        UUID userId = UUID.randomUUID();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO auth.auth_user (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setObject(1, userId);
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setBoolean(4, user.getEnabled());
            ps.setBoolean(5, user.getAccountNonExpired());
            ps.setBoolean(6, user.getAccountNonLocked());
            ps.setBoolean(7, user.getCredentialsNonExpired());
            ps.executeUpdate();
            user.setId(userId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE auth.auth_user SET password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? WHERE id = ?")) {
            ps.setString(1, user.getPassword());
            ps.setBoolean(2, user.getEnabled());
            ps.setBoolean(3, user.getAccountNonExpired());
            ps.setBoolean(4, user.getAccountNonLocked());
            ps.setBoolean(5, user.getCredentialsNonExpired());
            ps.setObject(6, user.getId());
            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(String id) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM auth.auth_user WHERE id = ?")) {
            ps.setObject(1, UUID.fromString(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(AuthUserEntityRowMapper.instance.mapRow(rs, 1));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID", e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM auth.auth_user WHERE username = ?",
                            AuthUserEntityRowMapper.instance,
                            username
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity user) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM auth.auth_user WHERE id = ?")) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user", e);
        }
    }
}
