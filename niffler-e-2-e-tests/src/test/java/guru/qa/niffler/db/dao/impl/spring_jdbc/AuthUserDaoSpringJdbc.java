package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc extends AbstractSpringDao<AuthUserEntity> implements AuthUserDao {

    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthUserDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, AuthUserEntityRowMapper.INSTANCE);
    }

    public AuthUserDaoSpringJdbc(DataSource dataSource) {
        super(dataSource, AuthUserEntityRowMapper.INSTANCE);
    }

    @Override
    public AuthUserEntity create(AuthUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        String password = passwordEncoder.encode(entity.getPassword());
        return jdbcTemplate.queryForObject(sql, rowMapper,
                entity.getUsername(), password, entity.getEnabled(), entity.getAccountNonExpired(),
                entity.getAccountNonLocked(), entity.getCredentialsNonExpired()
        );
    }

    @Override
    public AuthUserEntity update(AuthUserEntity entity) {
        String sql = "UPDATE category SET username = ?, password = ?, enabled = ?, " +
                "account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? WHERE id = ? RETURNING *";
        String password = passwordEncoder.encode(entity.getPassword());
        return jdbcTemplate.queryForObject(sql, rowMapper,
                entity.getUsername(), password, entity.getEnabled(), entity.getAccountNonExpired(),
                entity.getAccountNonLocked(), entity.getCredentialsNonExpired(), entity.getId()
        );
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        AuthUserEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        AuthUserEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, username);
        return Optional.ofNullable(entity);
    }

    @Override
    public boolean delete(AuthUserEntity entity) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public List<AuthUserEntity> findAll() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
