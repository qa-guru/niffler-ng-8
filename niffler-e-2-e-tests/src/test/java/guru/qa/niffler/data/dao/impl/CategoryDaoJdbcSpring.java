package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbcSpring implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    public CategoryDaoJdbcSpring(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public CategoryEntity create(CategoryEntity category) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (username, name, archived) " +
                            "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public CategoryEntity update(CategoryEntity category) {
        jdbcTemplate.update(
                "UPDATE category SET username = ?, name = ?, archived = ? WHERE id = ?",
                ps -> {
                    ps.setString(1, category.getUsername());
                    ps.setString(2, category.getName());
                    ps.setBoolean(3, category.isArchived());
                    ps.setObject(4, category.getId());
                }
        );
        return category;
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM category WHERE id = ?",
                CategoryEntityRowMapper.instance,
                id
        ));
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM category WHERE username = ? AND name = ?",
                CategoryEntityRowMapper.instance,
                username,
                categoryName
        ));
    }

    @Override
    public List<CategoryEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.instance
        );
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        jdbcTemplate.update(
                "DELETE FROM category WHERE id = ?",
                category.getId()
        );
    }
}
