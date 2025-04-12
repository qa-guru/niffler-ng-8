package guru.qa.niffler.db;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Databases {

    private Databases() {
    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, Connection>> threadConnection = new ConcurrentHashMap<>();
    private static final UserTransactionManager utm;

    static {
        utm = new UserTransactionManager();
        try {
            utm.init();
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataSource dataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                url -> {
                    AtomikosDataSourceBean atomikosDs = new AtomikosDataSourceBean();
                    String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
                    atomikosDs.setUniqueResourceName(uniqId);
                    atomikosDs.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.put("URL", jdbcUrl);
                    props.put("user", "postgres");
                    props.put("password", "secret");
                    atomikosDs.setXaProperties(props);
                    atomikosDs.setMaxPoolSize(12);
                    atomikosDs.setMinPoolSize(6);
                    return atomikosDs;
                }
        );
    }

    public static Connection connection(String jdbcUrl) {
        return threadConnection.computeIfAbsent(
                Thread.currentThread().threadId(),
                key -> {
                    try {
                        return new HashMap<>(Map.of(
                                jdbcUrl,
                                dataSource(jdbcUrl).getConnection()
                        ));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).computeIfAbsent(
                jdbcUrl,
                key -> {
                    try {
                        return dataSource(jdbcUrl).getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static void closeAllConnection() {
        for (Map<String, Connection> connectionMap : threadConnection.values()) {
            for (Connection connection : connectionMap.values()) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException ignored) {
                    //NOP
                }
            }
        }
    }

    public static void closeTransactionManager() {
        utm.close();
    }

}
