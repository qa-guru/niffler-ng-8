package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.interfaces.CategoryDao;
import guru.qa.niffler.data.entity.category.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into \"category\" (name, username, archived)" +
                            " values (?, ? , ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());

            return ps;
        }, keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"category\"",
                CategoryEntityRowMapper.INSTANCE
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return Optional.empty();
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        return List.of();
    }

    @Override
    public void deleteCategory(CategoryEntity category) {

    }
}