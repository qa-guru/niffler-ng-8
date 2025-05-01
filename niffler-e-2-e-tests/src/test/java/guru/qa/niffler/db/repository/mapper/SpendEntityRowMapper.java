package guru.qa.niffler.db.repository.mapper;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityRowMapper {

    public static final SpendEntityRowMapper INSTANCE = new SpendEntityRowMapper();

    private SpendEntityRowMapper() {
    }

    public SpendEntity mapRow(ResultSet rs) throws SQLException {
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
