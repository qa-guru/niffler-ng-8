package guru.qa.niffler.data;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataBases {
    private DataBases() {
    }

    public static final Map<String, DataSource> datasource = new ConcurrentHashMap<>();


    public static DataSource dataSources(String jdbcUrl) {
        return datasource.computeIfAbsent(
                jdbcUrl,
                key -> {
                    PGSimpleDataSource ds = new PGSimpleDataSource();
                    ds.setUser("postgres");
                    ds.setPassword("secret");
                    ds.setUrl(key);
                    return ds;
                }
        );
    }

    public static Connection connection(String jbcUrl) throws SQLException {
        return dataSources(jbcUrl).getConnection();
    }


}
