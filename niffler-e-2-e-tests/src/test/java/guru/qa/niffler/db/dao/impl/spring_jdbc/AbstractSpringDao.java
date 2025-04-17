package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class AbstractSpringDao<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    public AbstractSpringDao(String jdbcUrl, RowMapper<T> rowMapper) {
        this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(jdbcUrl));
        this.rowMapper = rowMapper;
    }

}
