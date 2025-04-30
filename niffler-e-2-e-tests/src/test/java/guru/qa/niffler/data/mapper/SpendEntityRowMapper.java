package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

    public static final SpendEntityRowMapper instance = new SpendEntityRowMapper();

    private SpendEntityRowMapper() {
    }

    @Override
    public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpendEntity spend = new SpendEntity();
        spend.setId(rs.getObject("spend_id", UUID.class));
        spend.setUsername(rs.getString("username"));
        spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        spend.setSpendDate(rs.getDate("spend_date"));
        spend.setAmount(rs.getDouble("amount"));
        spend.setDescription(rs.getString("description"));
        return spend;
    }
}

