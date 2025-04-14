package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.AuthAuthorityEntityRowMapper;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private static final AuthAuthorityEntityRowMapper AUTHORITY_ROW_MAPPER = AuthAuthorityEntityRowMapper.INSTANCE;
    private final JdbcTemplate jdbcTemplate;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public AuthAuthorityEntity createAuthAuthority(AuthAuthorityEntity entity) {
        String sql = "INSERT INTO category (user_id, authority) " +
                "VALUES(?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, AUTHORITY_ROW_MAPPER, entity.getUser().getId(), entity.getAuthority());
    }

    @Override
    public AuthAuthorityEntity updateAuthAuthority(AuthAuthorityEntity entity) {
        String sql = "UPDATE authority SET user_id = ?, authority = ? WHERE id = ? RETURNING *";
        return jdbcTemplate.queryForObject(sql, AUTHORITY_ROW_MAPPER, entity.getUser(), entity.getAuthority(), entity.getId());
    }

    @Override
    public Optional<AuthAuthorityEntity> findAuthAuthorityById(UUID id) {
        String sql = "SELECT * FROM authority WHERE id = ?";
        AuthAuthorityEntity entity = jdbcTemplate.queryForObject(sql, AUTHORITY_ROW_MAPPER, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<AuthAuthorityEntity> findAuthAuthorityByUserId(UUID userId) {
        String sql = "SELECT * FROM authority WHERE user_id = ?";
        return jdbcTemplate.query(sql, AUTHORITY_ROW_MAPPER, userId);
    }

    @Override
    public boolean deleteAuthAuthority(AuthAuthorityEntity entity) {
        String sql = "DELETE FROM authority WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

}
