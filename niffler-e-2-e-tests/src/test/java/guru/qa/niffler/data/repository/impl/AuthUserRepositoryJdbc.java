package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.enums.AuthorityRoles;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    public Optional<AuthUserEntity> findById(UUID uuid) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u join authority a on u.id = a.user_id where u.id = ?"
        )) {
            userPs.setObject(1, uuid);

            userPs.execute();

            try (ResultSet rs = userPs.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthAuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.INSTANCE.mapRow(rs, 1);
                    }

                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setId(rs.getObject("a.id", UUID.class));
                    ae.setUser(user);
                    ae.setAuthority(AuthorityRoles.valueOf(rs.getString("authority")));
                    authorityEntities.add(ae);

                    AuthUserEntity result = new AuthUserEntity();
                    result.setId(rs.getObject("id", UUID.class));
                    result.setUsername(rs.getString("username"));
                    result.setPassword(rs.getString("password"));
                    result.setEnabled(rs.getBoolean("enabled"));
                    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                }
                if (user != null) {
                    return Optional.empty();
                } else{
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }

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

    public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID,AuthUserEntity> userMap = new ConcurrentHashMap<>();
        UUID userId = null;
        while (rs.next()) {
            userId = rs.getObject("id", UUID.class);
            AuthUserEntity user = userMap.computeIfAbsent(userId, id ->{
                ///
                return null;
            });

            AuthAuthorityEntity authority = new AuthAuthorityEntity();
            authority.setId(rs.getObject("authority_id", UUID.class));
            authority.setAuthority(AuthorityRoles.valueOf(rs.getString("authority")));
            user.getAuthorities().add(authority);
        }
        return userMap.get(userId);
    }
}