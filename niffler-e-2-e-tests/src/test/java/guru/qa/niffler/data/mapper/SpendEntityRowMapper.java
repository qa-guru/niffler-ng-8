package guru.qa.niffler.data.mapper;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class SpendEntityRowMapper implements RowMapper<SpendEntity> {
  public static final SpendEntityRowMapper instance = new SpendEntityRowMapper();

  private SpendEntityRowMapper() {
  }

  @Override
  public @Nonnull SpendEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
    SpendEntity result = new SpendEntity();
    result.setId(rs.getObject("id", UUID.class));
    result.setUsername(rs.getString("username"));
    result.setSpendDate(rs.getDate("spend_date"));
    result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    result.setAmount(rs.getDouble("amount"));
    result.setDescription(rs.getString("description"));
    result.setCategory(null);
    return result;
  }
}