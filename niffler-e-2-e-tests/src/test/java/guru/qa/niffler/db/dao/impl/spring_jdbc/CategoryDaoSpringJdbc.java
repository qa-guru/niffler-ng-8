package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.db.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private static final CategoryEntityRowMapper CATEGORY_ROW_MAPPER = CategoryEntityRowMapper.INSTANCE;
    private final JdbcTemplate jdbcTemplate;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity entity) {
        String sql = "INSERT INTO category (username, name, archived) VALUES(?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, CATEGORY_ROW_MAPPER,
                entity.getUsername(), entity.getName(), entity.isArchived()
        );
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity entity) {
        String sql = "UPDATE category SET username = ?, name = ?, archived = ? WHERE id = ? RETURNING *";
        return jdbcTemplate.queryForObject(sql, CATEGORY_ROW_MAPPER,
                entity.getUsername(), entity.getName(), entity.isArchived(), entity.getId()
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        CategoryEntity entity = jdbcTemplate.queryForObject(sql, CATEGORY_ROW_MAPPER, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByNameAndUsername(String name, String username) {
        String sql = "SELECT * FROM category WHERE name = ? and username = ?";
        CategoryEntity entity = jdbcTemplate.queryForObject(sql, CATEGORY_ROW_MAPPER, name, username);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<CategoryEntity> findAllCategoryByUsername(String username) {
        String sql = "SELECT * FROM category WHERE username = ?";
        return jdbcTemplate.query(sql, CATEGORY_ROW_MAPPER, username);
    }

    @Override
    public boolean deleteCategory(CategoryEntity entity) {
        String sql = "DELETE FROM category WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public List<CategoryEntity> findAllCategories() {
        String sql = "SELECT * FROM category";
        return jdbcTemplate.query(sql, CATEGORY_ROW_MAPPER);
    }

}
