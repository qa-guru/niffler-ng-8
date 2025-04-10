package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.dao.AbstractDao;
import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc extends AbstractDao<CategoryEntity> implements CategoryDao {

    public CategoryDaoJdbc() {
        super(CFG.spendJdbcUrl());
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity entity) {
        String sql = "INSERT INTO category (username, name, archived) VALUES(?, ?, ?) RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getName(), entity.isArchived());
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity entity) {
        String sql = "UPDATE category SET username = ?, name = ?, archived = ? WHERE id = ? RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getName(), entity.isArchived(), entity.getId());
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        return executeQueryToOptional(sql, id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByNameAndUsername(String name, String username) {
        String sql = "SELECT * FROM category WHERE name = ? and username = ?";
        return executeQueryToOptional(sql, name, username);
    }

    @Override
    public List<CategoryEntity> findAllCategoryByUsername(String username) {
        String sql = "SELECT * FROM category WHERE username = ?";
        return executeQueryToList(sql, username);
    }

    @Override
    public boolean deleteCategory(CategoryEntity entity) {
        String sql = "DELETE FROM category WHERE id = ?";
        return executeUpdateToBoolean(sql, entity.getId());
    }

    @Override
    protected CategoryEntity mapResultSet(ResultSet rs) throws SQLException {
        return new CategoryEntity()
                .setId(rs.getObject("id", UUID.class))
                .setName(rs.getString("name"))
                .setUsername(rs.getString("username"))
                .setArchived(rs.getBoolean("archived"));
    }

}