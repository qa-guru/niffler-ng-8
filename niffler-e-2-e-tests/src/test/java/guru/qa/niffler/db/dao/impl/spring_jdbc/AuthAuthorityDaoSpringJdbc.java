package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.AuthAuthorityEntityRowMapper;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc extends AbstractSpringDao<AuthAuthorityEntity> implements AuthAuthorityDao {

    public AuthAuthorityDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, AuthAuthorityEntityRowMapper.INSTANCE);
    }

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        super(dataSource, AuthAuthorityEntityRowMapper.INSTANCE);
    }

    @Override
    public AuthAuthorityEntity create(AuthAuthorityEntity entity) {
        String sql = "INSERT INTO category (user_id, authority) " +
                "VALUES(?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper, entity.getUser().getId(), entity.getAuthority());
    }

    @Override
    public AuthAuthorityEntity update(AuthAuthorityEntity entity) {
        String sql = "UPDATE authority SET user_id = ?, authority = ? WHERE id = ? RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper, entity.getUser(), entity.getAuthority(), entity.getId());
    }

    @Override
    public Optional<AuthAuthorityEntity> findById(UUID id) {
        String sql = "SELECT * FROM authority WHERE id = ?";
        AuthAuthorityEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<AuthAuthorityEntity> findByUserId(UUID userId) {
        String sql = "SELECT * FROM authority WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public boolean delete(AuthAuthorityEntity entity) {
        String sql = "DELETE FROM authority WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        String sql = "SELECT * FROM authority";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
