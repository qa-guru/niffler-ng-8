package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.spring.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.CategoryEntityExtractor;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositorySpringJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();
    private static final SpendDao spendDao = new SpendDaoSpringJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

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
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                   SELECT *
                                    FROM category
                                    WHERE id = ?
                                """,
                        CategoryEntityExtractor.instance,
                        id
                )
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                   SELECT *
                                    FROM category
                                    WHERE username = ? and name = ?
                                """,
                        CategoryEntityExtractor.instance,
                        username,
                        name
                )
        );
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                   SELECT s.id, s.username, s.spend_date, s.currency, s.amount, s.description, c.id as category_id,
                                   c.name as category_name, c.archived as category_archived
                                   FROM spend s JOIN category c on s.category_id = c.id
                                   WHERE s.id = ?
                                """,
                        SpendEntityExtractor.instance,
                        id
                )
        );
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                   SELECT s.id, s.username, s.spend_date, s.currency, s.amount, s.description, c.id as category_id,
                                   c.name as category_name, c.archived as category_archived
                                   FROM spend s JOIN category c on s.category_id = c.id
                                   WHERE s.username = ? and s.description = ?
                                """,
                        SpendEntityExtractor.instance,
                        username,
                        description
                )
        );
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