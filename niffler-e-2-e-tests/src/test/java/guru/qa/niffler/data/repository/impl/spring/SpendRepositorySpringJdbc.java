package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        if (spend.getCategory().getId() == null) {
            jdbcTemplate.update(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO category (username, name, archived) " +
                                        "VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, spend.getCategory().getUsername());
                        ps.setString(2, spend.getCategory().getName());
                        ps.setBoolean(3, spend.getCategory().isArchived());
                        return ps;
                    }, kh
            );
            final UUID generatedKeyOfCategory = (UUID) kh.getKeys().get("id");
            spend.getCategory().setId(generatedKeyOfCategory);
        }
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                                    "VALUES (?, ?, ?, ?, ?, ? )",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, spend.getUsername());
                    ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                    ps.setString(3, spend.getCurrency().name());
                    ps.setDouble(4, spend.getAmount());
                    ps.setString(5, spend.getDescription());
                    ps.setObject(6, spend.getCategory().getId());
                    return ps;
                }, kh
        );
        final UUID generatedKeyOfSpend = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKeyOfSpend);
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
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
}