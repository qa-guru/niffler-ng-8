package guru.qa.niffler.data.mapper;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.dataSource;

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
                new CategoryDaoSpringJdbc(dataSource(Config.getInstance().spendJdbcUrl()))
                        .findCategoryById(
                                rs.getObject("category_id", UUID.class)
                        )
                        .get()
        );
        return se;
    }
}
