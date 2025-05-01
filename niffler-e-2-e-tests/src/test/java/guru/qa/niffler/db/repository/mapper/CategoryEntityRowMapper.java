package guru.qa.niffler.db.repository.mapper;

import guru.qa.niffler.db.entity.spend.CategoryEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper {

    public static final CategoryEntityRowMapper INSTANCE = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {
    }

    public CategoryEntity mapRow(ResultSet rs) throws SQLException {
        return new CategoryEntity()
                .setId(rs.getObject("id", UUID.class))
                .setName(rs.getString("name"))
                .setUsername(rs.getString("username"))
                .setArchived(rs.getBoolean("archived"));
    }

}
