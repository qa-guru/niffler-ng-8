package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.dao.AbstractDao;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.sql.*;
import java.util.UUID;

public class SpendDaoJdbc extends AbstractDao<SpendEntity, UUID> implements SpendDao {

    public SpendDaoJdbc() {
        super(CFG.spendJdbcUrl());
    }

    public SpendEntity create(SpendEntity entity) {
        String sql = "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        UUID id = create(entity,
                sql,
                rs -> rs.getObject("id", UUID.class)
        );
        entity.setId(id);
        return entity;
    }

    public void fillCreatePrepareStatement(SpendEntity entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getUsername());
        ps.setDate(2, entity.getSpendDate());
        ps.setString(3, entity.getCurrency().name());
        ps.setDouble(4, entity.getAmount());
        ps.setString(5, entity.getDescription());
        ps.setObject(6, entity.getCategory().getId());
    }

    @Override
    protected SpendEntity mapResultSet(ResultSet rs) throws SQLException {
        return new SpendEntity(
                rs.getObject("id", UUID.class),
                rs.getDate("spend_date"),
                new CategoryEntity().setId(rs.getObject("category_id", UUID.class)),
                CurrencyValues.valueOf(rs.getString("currency")),
                rs.getDouble("amount"),
                rs.getString("username"),
                rs.getString("description")

        );
    }

}
