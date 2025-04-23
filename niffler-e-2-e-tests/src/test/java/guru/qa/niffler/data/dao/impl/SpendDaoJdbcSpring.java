package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbcSpring implements SpendDao {

    private final JdbcTemplate jdbcTemplate;


    public SpendDaoJdbcSpring(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public SpendEntity create(SpendEntity spend) {
        return jdbcTemplate.queryForObject("INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?) RETURNING *",
                SpendEntityRowMapper.instance,
                spend.getUsername(),
                spend.getSpendDate(),
                spend.getCurrency().name(),
                spend.getAmount(),
                spend.getDescription(),
                spend.getCategory().getId());
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM spend WHERE id = ?",
                SpendEntityRowMapper.instance,
                id
        ));
    }

    @Override
    public List<SpendEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM spend",
                SpendEntityRowMapper.instance
        );
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        jdbcTemplate.update(
                "DELETE FROM spend WHERE id = ?",
                spend.getId()
        );
    }
}
