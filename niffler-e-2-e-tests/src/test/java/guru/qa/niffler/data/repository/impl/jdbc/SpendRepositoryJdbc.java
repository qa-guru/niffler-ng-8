package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        if (spend.getCategory().getId() == null) {
            try {
                PreparedStatement categoryPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                        "INSERT INTO category (username, name, archived) " +
                                "VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                categoryPs.setString(1, spend.getCategory().getUsername());
                categoryPs.setString(2, spend.getCategory().getName());
                categoryPs.setBoolean(3, spend.getCategory().isArchived());
                categoryPs.executeUpdate();
                final UUID generatedKeyOfCategory;
                try (ResultSet rs = categoryPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKeyOfCategory = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Cant find id in ResultSet");
                    }
                }
                spend.getCategory().setId(generatedKeyOfCategory);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        try (
                PreparedStatement spendPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                                "VALUES (?, ?, ?, ?, ?, ? )",
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            spendPs.setString(1, spend.getUsername());
            spendPs.setDate(2, new Date(spend.getSpendDate().getTime()));
            spendPs.setString(3, spend.getCurrency().name());
            spendPs.setDouble(4, spend.getAmount());
            spendPs.setString(5, spend.getDescription());
            spendPs.setObject(6, spend.getCategory().getId());
            spendPs.executeUpdate();
            final UUID generatedKeyOfSpend;
            try (ResultSet rs = spendPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKeyOfSpend = rs.getObject("id", UUID.class);

                } else {
                    throw new SQLException("Cant find id in ResultSet");
                }
            }
            spend.setId(generatedKeyOfSpend);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                           SELECT s.id, s.username, s.spend_date, s.currency, s.amount, s.description, c.id as category_id,
                           c.name as category_name, c.archived as category_archived
                           FROM spend s JOIN category c on s.category_id = c.id
                           WHERE s.id = ?
                        """
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate((rs.getDate("spend_date")));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("category_id", UUID.class));
                    ce.setName(rs.getString("category_name"));
                    ce.setArchived(rs.getBoolean("category_archived"));
                    se.setCategory(ce);
                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}