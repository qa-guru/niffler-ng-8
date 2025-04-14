package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private static final AuthUserEntityRowMapper AUTH_USER_ROW_MAPPER = AuthUserEntityRowMapper.INSTANCE;
    private final JdbcTemplate jdbcTemplate;

    public AuthUserDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public AuthUserEntity createAuthUser(AuthUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        String password = passwordEncoder.encode(entity.getPassword());
        return jdbcTemplate.queryForObject(sql, AUTH_USER_ROW_MAPPER,
                entity.getUsername(), password, entity.getEnabled(), entity.getAccountNonExpired(),
                entity.getAccountNonLocked(), entity.getCredentialsNonExpired()
        );
    }

    @Override
    public AuthUserEntity updateAuthUser(AuthUserEntity entity) {
        String sql = "UPDATE category SET username = ?, password = ?, enabled = ?, " +
                "account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? WHERE id = ? RETURNING *";
        String password = passwordEncoder.encode(entity.getPassword());
        return jdbcTemplate.queryForObject(sql, AUTH_USER_ROW_MAPPER,
                entity.getUsername(), password, entity.getEnabled(), entity.getAccountNonExpired(),
                entity.getAccountNonLocked(), entity.getCredentialsNonExpired(), entity.getId()
        );
    }

    @Override
    public Optional<AuthUserEntity> findAuthUserById(UUID id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        AuthUserEntity entity = jdbcTemplate.queryForObject(sql, AUTH_USER_ROW_MAPPER, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<AuthUserEntity> findAuthUserByUsername(String username) {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        AuthUserEntity entity = jdbcTemplate.queryForObject(sql, AUTH_USER_ROW_MAPPER, username);
        return Optional.ofNullable(entity);
    }

    @Override
    public boolean deleteAuthUser(AuthUserEntity entity) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

}
