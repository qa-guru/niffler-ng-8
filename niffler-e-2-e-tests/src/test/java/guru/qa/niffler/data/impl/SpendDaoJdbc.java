package guru.qa.niffler.data.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {
    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (username, currency, spend_date, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, spend.getUsername());
                ps.setObject(2, spend.getCurrency().name());
                ps.setDate(3, spend.getSpendDate());
                ps.setDouble(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());
                ps.executeUpdate();
                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                        spend.setId(generatedKey);
                    } else {
                        throw new SQLException("Не удалось создать запись в таблице spend");
                    }
                }
                return spend;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spend WHERE id = ?"
            )) {
                ps.setObject(1, id);
                ps.executeUpdate();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        SpendEntity spend = new SpendEntity();
                        spend.setId(rs.getObject("id", UUID.class));
                        spend.setUsername(rs.getString("username"));
                        spend.setCategory(rs.getObject("currency", CategoryEntity.class));
                        spend.setSpendDate(rs.getDate("spend_date"));
                        spend.setAmount(rs.getDouble("amount"));
                        spend.setDescription(rs.getString("description"));
                        spend.setCategory(rs.getObject("category", CategoryEntity.class));
                        return Optional.of(spend);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendByUsernameAndCategoryName(String username) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spend WHERE username = ? LIMIT 1"
            )) {
                ps.setObject(1, username);
                ps.executeUpdate();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        SpendEntity spend = new SpendEntity();
                        spend.setId(rs.getObject("id", UUID.class));
                        spend.setUsername(rs.getString("username"));
                        spend.setCategory(rs.getObject("currency", CategoryEntity.class));
                        spend.setSpendDate(rs.getDate("spend_date"));
                        spend.setAmount(rs.getDouble("amount"));
                        spend.setDescription(rs.getString("description"));
                        spend.setCategory(rs.getObject("category", CategoryEntity.class));
                        return Optional.of(spend);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllSpendByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spend WHERE username = ? LIMIT 1"
            )) {
                ps.setObject(1, username);
                ps.executeUpdate();
                try (ResultSet rs = ps.getResultSet()) {
                    List<SpendEntity> spends = new ArrayList<>();
                    while (rs.next()) {
                        SpendEntity spend = new SpendEntity();
                        spend.setId(rs.getObject("id", UUID.class));
                        spend.setUsername(rs.getString("username"));
                        spend.setCategory(rs.getObject("currency", CategoryEntity.class));
                        spend.setSpendDate(rs.getDate("spend_date"));
                        spend.setAmount(rs.getDouble("amount"));
                        spend.setDescription(rs.getString("description"));
                        spend.setCategory(rs.getObject("category", CategoryEntity.class));
                        spends.add(spend);
                    }
                    return spends;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM category WHERE id = ?",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setObject(1, spend.getId());
                int count = ps.executeUpdate();
                if (count != 1) {
                    throw new SQLException("");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
