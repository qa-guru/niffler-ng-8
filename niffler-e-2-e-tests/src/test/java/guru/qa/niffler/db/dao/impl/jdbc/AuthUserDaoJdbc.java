package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc extends AbstractDao<AuthUserEntity> implements AuthUserDao {

    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthUserDaoJdbc(String jdbcUrl) {
        super(jdbcUrl);
    }

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        String password = passwordEncoder.encode(entity.getPassword());
        return executeQuery(sql, entity.getUsername(), password, entity.getEnabled(),
                entity.getAccountNonExpired(), entity.getAccountNonLocked(), entity.getCredentialsNonExpired());
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity entity) {
        String sql = "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, " +
                "account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? WHERE id = ? RETURNING *";
        String password = passwordEncoder.encode(entity.getPassword());
        return executeQuery(sql, entity.getUsername(), password, entity.getEnabled(),
                entity.getAccountNonExpired(), entity.getAccountNonLocked(), entity.getCredentialsNonExpired(), entity.getId());
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        return executeQueryToOptional(sql, id);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(String username) {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        return executeQueryToOptional(sql, username);
    }

    @Override
    public boolean delete(AuthUserEntity entity) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        return executeUpdateToBoolean(sql, entity.getId());
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        String sql = "SELECT * FROM \"user\"";
        return executeQueryToList(sql);
    }

    @Override
    protected @Nonnull AuthUserEntity mapResultSet(ResultSet rs) throws SQLException {
        return new AuthUserEntity()
                .setId(rs.getObject("id", UUID.class))
                .setUsername(rs.getString("username"))
                .setPassword(rs.getString("password"))
                .setEnabled(rs.getBoolean("enabled"))
                .setAccountNonExpired(rs.getBoolean("account_non_expired"))
                .setAccountNonLocked(rs.getBoolean("account_non_locked"))
                .setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    }

}
