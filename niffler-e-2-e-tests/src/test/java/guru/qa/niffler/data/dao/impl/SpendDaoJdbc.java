package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {
  private final Connection connection;

  public SpendDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public SpendEntity createSpend(SpendEntity spend) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, spend.getUsername());
      ps.setObject(2, spend.getSpendDate());
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategoryId());

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
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create spend", e);
    }
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM spend WHERE id = ?")) {
      ps.setObject(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          SpendEntity se = new SpendEntity();
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          se.setCategoryId(null);
          return Optional.of(se);
        }
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find spend by id", e);
    }
  }

  @Override
  public List<SpendEntity> findAllSpendsByUserName(String userName) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM spend WHERE username = ?")) {
      ps.setString(1, userName);

      try (ResultSet rs = ps.executeQuery()) {
        List<SpendEntity> spends = new ArrayList<>();
        while (rs.next()) {
          SpendEntity se = new SpendEntity();
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          se.setCategoryId(null);
          spends.add(se);
        }
        return spends;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all spends by username", e);
    }
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM spend WHERE id = ?")) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete spend", e);
    }
  }

  @Override
  public List<SpendEntity> findAll() {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM spend")) {

      try (ResultSet rs = ps.executeQuery()) {
        List<SpendEntity> spends = new ArrayList<>();
        while (rs.next()) {
          SpendEntity se = new SpendEntity();
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          se.setCategoryId(null);
          spends.add(se);
        }
        return spends;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all spends", e);
    }
  }
}
