package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNullApi;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private final JdbcTemplate jdbcTemplate;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(AuthorityEntity... authority) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUserId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public AuthorityEntity create(AuthorityEntity authority) {
        return null;
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<AuthorityEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"authority\"",
                AuthorityEntityRowMapper.instance
        );
    }

    @Override
    public List<AuthorityEntity> findByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public void delete(AuthorityEntity authority) {

    }
}
