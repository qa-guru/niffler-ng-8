package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.enums.AuthorityRoles;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity authUserEntity) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled,account_non_expired," +
                        "account_non_locked,credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        ); PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?, ?)"
        )) {
            userPs.setString(1, authUserEntity.getUsername());
            userPs.setString(2, ENCODER.encode(authUserEntity.getPassword()));
            userPs.setBoolean(3, authUserEntity.getEnabled());
            userPs.setBoolean(4, authUserEntity.getAccountNonExpired());
            userPs.setBoolean(5, authUserEntity.getAccountNonLocked());
            userPs.setBoolean(6, authUserEntity.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find id in ResultSet");
            }
            authUserEntity.setId(generatedKey);

            for (AuthAuthorityEntity authority : authUserEntity.getAuthorities()) {
                authorityPs.setObject(1, authority.getUser().getId());
                authorityPs.setString(2, authority.getAuthority().name());
                authorityPs.addBatch();
                authorityPs.clearParameters();
            }

            authorityPs.executeBatch();

            return authUserEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        List<AuthUserEntity> users = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM category")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthUserEntity au = new AuthUserEntity();
                    au.setId(rs.getObject("id", UUID.class));
                    au.setUsername(rs.getString("username"));
                    au.setPassword(ENCODER.encode(rs.getString("password")));
                    au.setEnabled(rs.getBoolean("enabled"));
                    au.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    au.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    au.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    users.add(au);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public void deleteAuthority(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM authority where user_id = ?")) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void deleteUser(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" where id = ?")) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT a.id as authority_id, authority, user_id as id, u.username, u.password, u.enabled," +
                                " u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                                "FROM \"user\" u join public.authority a on u.id = a.user_id WHERE u.id = ?",
                        new ResultSetExtractor<AuthUserEntity>() {
                            public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
                                Map<UUID, AuthUserEntity> userMap = new HashMap<>();
                                UUID userId = null;

                                while (rs.next()) {
                                    userId = rs.getObject("id", UUID.class);

                                    AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                                        AuthUserEntity userEntity = new AuthUserEntity();
                                        userEntity.setId(id);
                                        try {
                                            userEntity.setUsername(rs.getString("username"));
                                            userEntity.setPassword(rs.getString("password"));
                                            userEntity.setEnabled(rs.getBoolean("enabled"));
                                            userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                                            userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                                            userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return userEntity;
                                    });

                                    AuthAuthorityEntity authority = new AuthAuthorityEntity();
                                    authority.setId(rs.getObject("authority_id", UUID.class));
                                    authority.setAuthority(AuthorityRoles.valueOf(rs.getString("authority")));
                                    user.getAuthorities().add(authority);
                                }
                                return userMap.get(userId);
                            }
                        },
                        id
                )
        );

    }

    @Override
    public Optional<AuthUserEntity> findByName(String name) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username=?")) {
            ps.setString(1, name);
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AuthUserEntity aue = new AuthUserEntity();
                    aue.setId(rs.getObject("id", UUID.class));
                    aue.setUsername(rs.getString("username"));
                    aue.setPassword(rs.getString("password"));
                    aue.setEnabled(rs.getBoolean("enabled"));
                    aue.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    aue.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    aue.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(aue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}