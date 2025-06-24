package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.db.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CategoryDaoSpringJdbc extends AbstractSpringDao<CategoryEntity> implements CategoryDao {

    public CategoryDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, CategoryEntityRowMapper.INSTANCE);
    }

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        super(dataSource, CategoryEntityRowMapper.INSTANCE);
    }

    @Override
    public @Nonnull CategoryEntity create(CategoryEntity entity) {
        String sql = "INSERT INTO category (username, name, archived) VALUES(?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
                entity.getUsername(), entity.getName(), entity.isArchived()
        );
    }

    @Override
    public @Nonnull CategoryEntity update(CategoryEntity entity) {
        String sql = "UPDATE category SET username = ?, name = ?, archived = ? WHERE id = ? RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
                entity.getUsername(), entity.getName(), entity.isArchived(), entity.getId()
        );
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findById(UUID id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        CategoryEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findByNameAndUsername(String name, String username) {
        String sql = "SELECT * FROM category WHERE name = ? and username = ?";
        CategoryEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, name, username);
        return Optional.ofNullable(entity);
    }

    @Override
    public @Nullable List<CategoryEntity> findAllByUsername(String username) {
        String sql = "SELECT * FROM category WHERE username = ?";
        return jdbcTemplate.query(sql, rowMapper, username);
    }

    @Override
    public boolean delete(CategoryEntity entity) {
        String sql = "DELETE FROM category WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public @Nullable List<CategoryEntity> findAll() {
        String sql = "SELECT * FROM category";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
