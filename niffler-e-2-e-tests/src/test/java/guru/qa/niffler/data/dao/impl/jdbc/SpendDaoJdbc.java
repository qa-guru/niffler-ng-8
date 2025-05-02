package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.spendJdbcUrl();

    @Override
    public SpendEntity create(SpendEntity spend) {
            try (PreparedStatement ps = holder(url).connection().prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, spend.getUsername());
                ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                ps.setString(3, spend.getCurrency().name());
                ps.setDouble(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                spend.setId(generatedKey);
                return spend;
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement spendPs = holder(url).connection().prepareStatement(
                """
                        UPDATE spend
                            SET username = ?,
                            spend_date   = ?,
                            currency     = ?,
                            amount       = ?,
                            description  = ?,
                            category_id  = ?
                            WHERE id = ?
                        """);

             PreparedStatement categoryPs = holder(url).connection().prepareStatement(
                     """
                             INSERT INTO category (username, name, archived)
                              VALUES (?, ?, ?)
                              ON CONFLICT (username, name)
                                DO UPDATE SET archived = ?
                             """
             )
        ){
            spendPs.setString(1, spend.getUsername());
            spendPs.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            spendPs.setString(3, spend.getCurrency().name());
            spendPs.setDouble(4, spend.getAmount());
            spendPs.setString(5, spend.getDescription());
            spendPs.setObject(6, spend.getCategory().getId());
            spendPs.setObject(7, spend.getId());
            spendPs.executeUpdate();

            categoryPs.setObject(1, spend.getCategory().getUsername());
            categoryPs.setObject(2, spend.getCategory().getName());
            categoryPs.setObject(3, spend.getCategory().isArchived());
            categoryPs.setObject(4, spend.getCategory().isArchived());
            categoryPs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(
                            new CategoryDaoJdbc()
                                    .findCategoryById(
                                            rs.getObject("category_id", UUID.class)
                                    )
                                    .get()
                    );
                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<SpendEntity> list = new ArrayList<>();
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(
                            new CategoryDaoJdbc()
                                    .findCategoryById(
                                            rs.getObject("category_id", UUID.class)
                                    )
                                    .get()
                    );
                    list.add(se);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
            try (PreparedStatement ps = holder(url).connection().prepareStatement(
                    "DELETE FROM spend WHERE id = ?"
            )) {
                ps.setObject(1, spend.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<SpendEntity> list = new ArrayList<>();
                while (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(
                            new CategoryDaoJdbc()
                                    .findCategoryById(
                                            rs.getObject("category_id", UUID.class)
                                    )
                                    .get()
                    );
                    list.add(se);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}