package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.SpendEntityRowMapper;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDaoSpringJdbc extends AbstractSpringDao<SpendEntity> implements SpendDao {

    public SpendDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, SpendEntityRowMapper.INSTANCE);
    }

    public SpendDaoSpringJdbc(DataSource dataSource) {
        super(dataSource, SpendEntityRowMapper.INSTANCE);
    }

    @Override
    public @Nonnull SpendEntity create(SpendEntity entity) {
        String sql = "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
                entity.getUsername(), entity.getSpendDate(), entity.getCurrency().name(),
                entity.getAmount(), entity.getDescription(), entity.getCategory().getId()
        );
    }

    @Override
    public @Nonnull SpendEntity update(SpendEntity entity) {
        String sql = "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? " +
            "WHERE id = ? RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
            entity.getUsername(), entity.getSpendDate(), entity.getCurrency().name(),
            entity.getAmount(), entity.getDescription(), entity.getCategory().getId(), entity.getId()
        );
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(UUID id) {
        String sql = "SELECT * FROM spend WHERE id = ?";
        SpendEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        String sql = "SELECT * FROM spend WHERE username = ? AND description = ?";
        SpendEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, username, description);
        return Optional.ofNullable(entity);
    }

    @Override
    public @Nullable List<SpendEntity> findAllByUsername(String username) {
        String sql = "SELECT * FROM spend WHERE username = ?";
        return jdbcTemplate.query(sql, rowMapper, username);
    }

    @Override
    public @Nullable List<SpendEntity> findAllByCategoryId(UUID categoryId) {
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
    public @Nullable List<SpendEntity> findAll() {
        String sql = "SELECT * FROM spend";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
