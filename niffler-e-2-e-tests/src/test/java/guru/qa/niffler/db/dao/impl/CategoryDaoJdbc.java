package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.dao.AbstractDao;
import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc extends AbstractDao<CategoryEntity, UUID> implements CategoryDao {

    public CategoryDaoJdbc() {
        super(CFG.spendJdbcUrl());
    }

    @Override
    public CategoryEntity create(CategoryEntity entity) {
        String sql = "INSERT INTO category (username, name, archived) VALUES(?, ?, ?)";
        UUID id = create(entity,
                sql,
                rs -> rs.getObject("id", UUID.class)
        );
        entity.setId(id);
        return entity;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return findById(id, "SELECT FROM category id = ?");
    }

    @Override
    protected CategoryEntity mapResultSet(ResultSet rs) throws SQLException {
        return new CategoryEntity(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("username"),
                rs.getBoolean("archived")
        );
    }

    @Override
    protected void fillCreatePrepareStatement(CategoryEntity entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getUsername());
        ps.setString(2, entity.getName());
        ps.setBoolean(3, entity.isArchived());
    }

}