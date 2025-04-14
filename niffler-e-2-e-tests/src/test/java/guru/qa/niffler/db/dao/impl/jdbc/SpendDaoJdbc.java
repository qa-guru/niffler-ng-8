package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.dao.AbstractDao;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc extends AbstractDao<SpendEntity> implements SpendDao {

    public SpendDaoJdbc(String jdbcUrl) {
        super(jdbcUrl);
    }

    @Override
    public SpendEntity createSpend(SpendEntity entity) {
        String sql = "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getSpendDate(), entity.getCurrency().name(),
                entity.getAmount(), entity.getDescription(), entity.getCategory().getId());
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        String sql = "SELECT * FROM spend WHERE id = ?";
        return executeQueryToOptional(sql, id);
    }

    @Override
    public List<SpendEntity> findAllSpendByUsername(String username) {
        String sql = "SELECT * FROM spend WHERE username = ?";
        return executeQueryToList(sql, username);
    }

    @Override
    public List<SpendEntity> findAllSpendByCategoryId(UUID categoryId) {
        String sql = "SELECT * FROM spend WHERE category_id = ?";
        return executeQueryToList(sql, categoryId);
    }

    @Override
    public boolean deleteSpend(SpendEntity entity) {
        String sql = "DELETE FROM spend WHERE id = ?";
        return executeUpdateToBoolean(sql, entity.getId());
    }

    @Override
    public List<SpendEntity> findAllSpends() {
        String sql = "SELECT * FROM spend";
        return executeQueryToList(sql);
    }

    @Override
    protected SpendEntity mapResultSet(ResultSet rs) throws SQLException {
        return new SpendEntity()
                .setId(rs.getObject("id", UUID.class))
                .setSpendDate(rs.getDate("spend_date"))
                .setCategory(new CategoryEntity().setId(rs.getObject("category_id", UUID.class)))
                .setCurrency(CurrencyValues.valueOf(rs.getString("currency")))
                .setAmount(rs.getDouble("amount"))
                .setUsername(rs.getString("username"))
                .setDescription(rs.getString("description"));
    }

}
