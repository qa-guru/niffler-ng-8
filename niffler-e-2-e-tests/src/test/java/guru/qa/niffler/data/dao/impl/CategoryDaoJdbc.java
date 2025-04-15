package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public CategoryEntity create(CategoryEntity category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO category (username, name, archived) " +
              "VALUES (?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, category.getUsername());
        ps.setString(2, category.getName());
        ps.setBoolean(3, category.isArchived());

        ps.executeUpdate();

        final UUID generatedKey;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedKey = rs.getObject("id", UUID.class);
          } else {
            throw new SQLException("Can`t find id in ResultSet");
          }
        }
        category.setId(generatedKey);
        return category;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public CategoryEntity update(CategoryEntity category) {
    if (category.getId() == null) {
      throw new IllegalArgumentException("Category ID must not be null");
    }
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?"
      )) {
        ps.setString(1, category.getName());
        ps.setString(2, category.getUsername());
        ps.setBoolean(3, category.isArchived());
        ps.setObject(4, category.getId());
        int affectedRows = ps.executeUpdate();
        if (affectedRows == 0) {
          throw new SQLException("Updating category failed, no rows affected");
        }
      }
      return category;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT * FROM category WHERE id = ?"
      )) {
        ps.setObject(1, id);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setUsername(rs.getString("username"));
            ce.setName(rs.getString("name"));
            ce.setArchived(rs.getBoolean("archived"));
            return Optional.of(ce);
          } else {
            return Optional.empty();
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find category with id: " + id, e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM category WHERE username = ? AND name = ?"
      )) {
        ps.setObject(1, username);
        ps.setObject(2, categoryName);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setUsername(rs.getString("username"));
            ce.setName(rs.getString("name"));
            ce.setArchived(rs.getBoolean("archived"));
            return Optional.of(ce);
          } else {
            return Optional.empty();
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find category with username and categoryName: " + username + ", " + categoryName, e);
    }
  }

  @Override
  public List<CategoryEntity> findAllByUsername(String username) {

    List<CategoryEntity> ceList = new ArrayList<>();

    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM category WHERE username = ?"
      )) {
        ps.setObject(1, username);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          while (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setUsername(rs.getString("username"));
            ce.setName(rs.getString("name"));
            ce.setArchived(rs.getBoolean("archived"));
            ceList.add(ce);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find category with username: " + username, e);
    }
    return ceList;
  }

  @Override
  public void deleteCategory(CategoryEntity category) {
    if (category == null || category.getId() == null) {
      return;
    }
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM category WHERE id = ?"
      )) {
        ps.setObject(1, category.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting category with Id: " + category.getId(), e);
    }
  }
}
