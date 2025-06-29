package guru.qa.niffler.db.dao.impl.spring_jdbc.mapper;

import guru.qa.niffler.db.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper INSTANCE = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {
    }

    @Override
    public @Nonnull CategoryEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new CategoryEntity()
                .setId(rs.getObject("id", UUID.class))
                .setName(rs.getString("name"))
                .setUsername(rs.getString("username"))
                .setArchived(rs.getBoolean("archived"));
    }

}
