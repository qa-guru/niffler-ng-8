package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.SpendEntityRowMapper;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc extends AbstractSpringDao<SpendEntity> implements SpendDao {

    public SpendDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, SpendEntityRowMapper.INSTANCE);
    }

    @Override
    public SpendEntity create(SpendEntity entity) {
        String sql = "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
                entity.getUsername(), entity.getSpendDate(), entity.getCurrency().name(),
                entity.getAmount(), entity.getDescription(), entity.getCategory().getId()
        );
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        String sql = "SELECT * FROM spend WHERE id = ?";
        SpendEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        String sql = "SELECT * FROM spend WHERE username = ?";
        return jdbcTemplate.query(sql, rowMapper, username);
    }

    @Override
    public List<SpendEntity> findAllByCategoryId(UUID categoryId) {
        String sql = "SELECT * FROM spend WHERE category_id = ?";
        return jdbcTemplate.query(sql, rowMapper, categoryId);
    }

    @Override
    public boolean delete(SpendEntity entity) {
        String sql = "DELETE FROM spend WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public List<SpendEntity> findAll() {
        String sql = "SELECT * FROM spend";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
