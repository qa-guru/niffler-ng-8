package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.rest.CurrencyValues;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class SpendRepositoryJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();
    private static final SpendDao spendDao = new SpendDaoJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );
    @Override
    public SpendEntity create(SpendEntity spend) {
        return jdbcTxTemplate.execute(() -> {
            if (spend.getCategory().getId() == null) {
                CategoryEntity category = categoryDao.create(spend.getCategory());
                spend.setCategory(category);
            }
            return spendDao.create(spend);
        });
    }


    @Override
    public SpendEntity update(SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }



    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category WHERE username = ? and name = ?"
        )) {

            ps.setString(1, username);
            ps.setString(2, name);
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

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return jdbcTxTemplate.execute(() ->
                spendDao.findSpendById(id)
                        .map(spend -> {
                            categoryDao.findCategoryById(spend.getCategory().getId())
                                    .ifPresent(spend::setCategory);
                            return spend;
                        })
        );
    }


    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ? and description = ?"
        )) {
            Optional<SpendEntity> spendOpt;
            ps.setString(1, username);
            ps.setString(2, description);
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
                                    .orElse(null)
                    );
                    spendOpt = Optional.of(se);
                } else {
                    spendOpt = Optional.empty();
                }
            }
            return spendOpt.map(spend -> {
                categoryDao.findCategoryById(spend.getCategory().getId())
                        .ifPresent(spend::setCategory);
                return spend;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDao.deleteCategory(category);
    }
}