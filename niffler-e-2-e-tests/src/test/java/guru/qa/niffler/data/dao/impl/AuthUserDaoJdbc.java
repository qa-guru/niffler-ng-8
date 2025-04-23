package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {
    private final Connection connection;

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }


    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.isEnabled());
            ps.setBoolean(4, user.isAccountNonExpired());
            ps.setBoolean(5, user.isAccountNonLocked());
            ps.setBoolean(6, user.isCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setId(rs.getObject("id", UUID.class));
                    authUser.setUsername(rs.getString("username"));
                    authUser.setPassword(rs.getString("password"));
                    authUser.setEnabled(rs.getBoolean("enabled"));
                    authUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(authUser);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user with Id: " + id, e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {

        List<AuthUserEntity> authUserEntities = new ArrayList<>();

        try(PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {

                while (rs.next()) {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setId(rs.getObject("id", UUID.class));
                    authUser.setUsername(rs.getString("username"));
                    authUser.setPassword(rs.getString("password"));
                    authUser.setEnabled(rs.getBoolean("enabled"));
                    authUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    authUserEntities.add(authUser);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authUserEntities;
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setId(rs.getObject("id", UUID.class));
                    authUser.setUsername(rs.getString("username"));
                    authUser.setPassword(rs.getString("password"));
                    authUser.setEnabled(rs.getBoolean("enabled"));
                    authUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(authUser);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user with username: " + username, e);
        }
    }

    @Override
    public void delete(AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user with Id:  " + user.getId(), e);
        }
    }
}
