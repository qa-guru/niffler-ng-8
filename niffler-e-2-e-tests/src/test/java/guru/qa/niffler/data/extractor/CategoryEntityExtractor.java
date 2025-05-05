package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityExtractor implements ResultSetExtractor<CategoryEntity> {
    public static final CategoryEntityExtractor instance = new CategoryEntityExtractor();

    private CategoryEntityExtractor() {
    }

    @Override
    public CategoryEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()) {
            CategoryEntity category = new CategoryEntity();
            category.setId(rs.getObject("id", UUID.class));
            category.setUsername(rs.getString("username"));
            category.setName(rs.getString("name"));
            category.setArchived(rs.getBoolean("archived"));
            return category;
        }
        return null;
    }
}