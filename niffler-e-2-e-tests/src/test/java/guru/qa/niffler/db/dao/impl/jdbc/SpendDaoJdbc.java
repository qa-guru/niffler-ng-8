package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDaoJdbc extends AbstractDao<SpendEntity> implements SpendDao {

    public SpendDaoJdbc(String jdbcUrl) {
        super(jdbcUrl);
    }

    @Override
    public @Nonnull SpendEntity create(SpendEntity entity) {
        String sql = "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES(?, ?, ?, ?, ?, ?) RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getSpendDate(), entity.getCurrency().name(),
                entity.getAmount(), entity.getDescription(), entity.getCategory().getId());
    }

    @Override
    public @Nonnull SpendEntity update(SpendEntity entity) {
        String sql = "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? " +
            "WHERE id = ? RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getSpendDate(), entity.getCurrency().name(),
            entity.getAmount(), entity.getDescription(), entity.getCategory().getId(), entity.getId());
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(UUID id) {
        String sql = "SELECT * FROM spend WHERE id = ?";
        return executeQueryToOptional(sql, id);
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        String sql = "SELECT * FROM spend WHERE username = ? AND description = ?";
        return executeQueryToOptional(sql, username, description);
    }

    @Override
    public @Nonnull List<SpendEntity> findAllByUsername(String username) {
        String sql = "SELECT * FROM spend WHERE username = ?";
        return executeQueryToList(sql, username);
    }

    @Override
    public @Nonnull List<SpendEntity> findAllByCategoryId(UUID categoryId) {
        String sql = "SELECT * FROM spend WHERE category_id = ?";
        return executeQueryToList(sql, categoryId);
    }

    @Override
    public boolean delete(SpendEntity entity) {
        String sql = "DELETE FROM spend WHERE id = ?";
        return executeUpdateToBoolean(sql, entity.getId());
    }

    @Override
    public @Nonnull List<SpendEntity> findAll() {
        String sql = "SELECT * FROM spend";
        return executeQueryToList(sql);
    }

    @Override
    protected @Nonnull SpendEntity mapResultSet(ResultSet rs) throws SQLException {
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
