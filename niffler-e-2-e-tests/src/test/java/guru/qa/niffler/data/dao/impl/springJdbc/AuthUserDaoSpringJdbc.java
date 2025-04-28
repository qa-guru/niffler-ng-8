package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.data.dao.interfaces.AuthUserDao;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private DataSource dataSource;

    public AuthUserDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public AuthUserEntity create(AuthUserEntity authUserEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                            " values (?, ? , ? , ? , ? , ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, authUserEntity.getUsername());
            ps.setString(2, authUserEntity.getPassword());
            ps.setBoolean(3, authUserEntity.getEnabled());
            ps.setBoolean(4, authUserEntity.getAccountNonExpired());
            ps.setBoolean(5, authUserEntity.getAccountNonLocked());
            ps.setBoolean(6, authUserEntity.getCredentialsNonExpired());

            return ps;
        }, keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        authUserEntity.setId(generatedKey);
        return authUserEntity;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID uuid) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM \"user\" WHERE id = ?",
                            AuthUserEntityRowMapper.INSTANCE,
                            uuid
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.INSTANCE
        );
    }
}