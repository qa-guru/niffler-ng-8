package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

  private static final Config CFG = Config.getInstance();

  private final Connection connection;

  public CategoryDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public CategoryEntity create(CategoryEntity category) {
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
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
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
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public CategoryEntity update(CategoryEntity category) {
    try (PreparedStatement ps = connection.prepareStatement(
            "UPDATE category SET name = ?, archived = ? WHERE id = ?"
    )) {
      ps.setString(1, category.getName());
      ps.setBoolean(2, category.isArchived());
      ps.setObject(3, category.getId());

      int rowsAffected = ps.executeUpdate();
      if (rowsAffected > 0) {
        return category;  // Возвращаем обновленную категорию
      } else {
        throw new SQLException("Category with ID " + category.getId() + " not found.");
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
