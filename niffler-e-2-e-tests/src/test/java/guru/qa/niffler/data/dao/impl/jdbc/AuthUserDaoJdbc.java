package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.data.dao.interfaces.AuthUserDao;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final Connection connection;

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity authUserEntity) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled,account_non_expired," +
                        "account_non_locked,credentials_non_expired)" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, authUserEntity.getUsername());
            ps.setString(2, ENCODER.encode(authUserEntity.getPassword()));
            ps.setBoolean(3, authUserEntity.getEnabled());
            ps.setBoolean(4, authUserEntity.getAccountNonExpired());
            ps.setBoolean(5, authUserEntity.getAccountNonLocked());
            ps.setBoolean(6, authUserEntity.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find id in ResultSet");
            }
            authUserEntity.setId(generatedKey);
            return authUserEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<AuthUserEntity> findAll() {
        return List.of();
    }
}