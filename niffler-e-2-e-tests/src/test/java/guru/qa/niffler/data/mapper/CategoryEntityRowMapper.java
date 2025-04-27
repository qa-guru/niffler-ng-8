package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.category.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper INSTANCE = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {
    }

    @Override
    public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryEntity category = new CategoryEntity();
        category.setId(UUID.fromString(rs.getString("id")));
        category.setName(rs.getString("name"));
        category.setUsername(rs.getString("username"));
        category.setArchived(rs.getBoolean("archived"));
        return category;
    }
}