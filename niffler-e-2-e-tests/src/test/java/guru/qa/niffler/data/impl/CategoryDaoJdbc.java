package guru.qa.niffler.data.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.CategoryEntity;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {
    private static final Config CFG = Config.getInstance();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (name, username, archived) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, category.getName());
                ps.setString(2, category.getUsername());
                ps.setBoolean(3, category.isArchived());
                ps.executeUpdate();
                final UUID generatedKey;
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                        category.setId(generatedKey);
                    } else {
                        throw new SQLException("В ResultSet-е не найдены значения");
                    }
                }
                return category;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username) {
        return Optional.empty();
    }

    @Override
    public List<CategoryEntity> findAllCategoryByUsername(String username) {
        return List.of();
    }

    @Override
    public void deleteCategory(CategoryEntity category) {

    }
}
