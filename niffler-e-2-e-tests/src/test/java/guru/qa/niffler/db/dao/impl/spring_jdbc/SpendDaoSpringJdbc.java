package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.SpendEntityRowMapper;
import guru.qa.niffler.db.entity.spend.SpendEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

    private static final SpendEntityRowMapper SPEND_ROW_MAPPER = SpendEntityRowMapper.INSTANCE;
    private final JdbcTemplate jdbcTemplate;

    public SpendDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public SpendEntity createSpend(SpendEntity entity) {
        String sql = "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, SPEND_ROW_MAPPER,
                entity.getUsername(), entity.getSpendDate(), entity.getCurrency().name(),
                entity.getAmount(), entity.getDescription(), entity.getCategory().getId()
        );
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        String sql = "SELECT * FROM spend WHERE id = ?";
        SpendEntity entity = jdbcTemplate.queryForObject(sql, SPEND_ROW_MAPPER, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<SpendEntity> findAllSpendByUsername(String username) {
        String sql = "SELECT * FROM spend WHERE username = ?";
        return jdbcTemplate.query(sql, SPEND_ROW_MAPPER, username);
    }

    @Override
    public List<SpendEntity> findAllSpendByCategoryId(UUID categoryId) {
        String sql = "SELECT * FROM spend WHERE category_id = ?";
        return jdbcTemplate.query(sql, SPEND_ROW_MAPPER, categoryId);
    }

    @Override
    public boolean deleteSpend(SpendEntity entity) {
        String sql = "DELETE FROM spend WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public List<SpendEntity> findAllSpends() {
        String sql = "SELECT * FROM spend";
        return jdbcTemplate.query(sql, SPEND_ROW_MAPPER);
    }

}
