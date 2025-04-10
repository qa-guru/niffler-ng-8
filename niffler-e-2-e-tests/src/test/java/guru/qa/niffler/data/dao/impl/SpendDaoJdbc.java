package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public SpendEntity createSpend(SpendEntity spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, spend.getUsername());
        ps.setObject(2, spend.getSpendDate());
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());

        ps.executeUpdate();

        final UUID generatedId;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedId = rs.getObject(1, UUID.class);
          } else {
            throw new SQLException("Failed to retrieve generated key");
          }
        }
        spend.setId(generatedId);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create spend", e);
    }
    return spend;
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT id, username, spend_date, currency, amount, description, category_id FROM spend WHERE id = ?")) {
        ps.setObject(1, id);

        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            SpendEntity se = new SpendEntity();
            CategoryEntity ce = new CategoryDaoJdbc()
                .findCategoryById(rs.getObject("category_id", UUID.class))
                .orElse(null);
            se.setId(rs.getObject("id", UUID.class));
            se.setUsername(rs.getString("username"));
            se.setSpendDate(rs.getDate("spend_date"));
            se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            se.setAmount(rs.getDouble("amount"));
            se.setDescription(rs.getString("description"));
            se.setCategory(ce);
            return Optional.of(se);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find spend by id", e);
    }
    return Optional.empty();
  }

  @Override
  public List<SpendEntity> findAllSpendsByUserName(String userName) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT id, username, spend_date, currency, amount, description, category_id FROM spend WHERE username = ?")) {
        ps.setString(1, userName);

        try (ResultSet rs = ps.executeQuery()) {
          List<SpendEntity> spends = new ArrayList<>();
          while (rs.next()) {
            SpendEntity spend = new SpendEntity();
            CategoryEntity ce = new CategoryDaoJdbc()
                .findCategoryById(rs.getObject("category_id", UUID.class))
                .orElse(null);
            spend.setId(rs.getObject("id", UUID.class));
            spend.setUsername(rs.getString("username"));
            spend.setSpendDate(rs.getDate("spend_date"));
            spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            spend.setAmount(rs.getDouble("amount"));
            spend.setDescription(rs.getString("description"));
            spend.setCategory(ce);
            spends.add(spend);
          }
          return spends;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all spends by username", e);
    }
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM spend WHERE id = ?")) {
        ps.setObject(1, spend.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete spend", e);
    }
  }
}
