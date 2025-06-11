package guru.qa.niffler.data;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Databases {
    private Databases() {}

    private static final Map<String, DataSource> datasources = new HashMap<>();

    public static Connection connection(String jdbcUrl) throws SQLException {
        return datasource(jdbcUrl).getConnection();
    }

    private static DataSource datasource(String jdbcUrl) {
        return datasources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
                    pgDataSource.setUser("postgres");
                    pgDataSource.setPassword("secret");
                    pgDataSource.setURL(key);
                    return pgDataSource;
                }
        );
    }
}
