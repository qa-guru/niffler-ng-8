package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.dao.impl.spring.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.currency.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendEntityRowMapper  implements RowMapper<SpendEntity> {
    public static final SpendEntityRowMapper instance = new SpendEntityRowMapper();

    private SpendEntityRowMapper(){}

    @Override
    public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpendEntity se = new SpendEntity();
        se.setId(rs.getObject("id", UUID.class));
        se.setUsername(rs.getString("username"));
        se.setCurrency(rs.getObject("currency", CurrencyValues.class));
        se.setAmount(rs.getDouble("amount"));
        se.setDescription(rs.getString("description"));
        se.setCategory(
                new CategoryDaoSpringJdbc()
                        .findCategoryById(
                                rs.getObject("category_id", UUID.class)
                        )
                        .get()
        );
        return se;
    }
}
