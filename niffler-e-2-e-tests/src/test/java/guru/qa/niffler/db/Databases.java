package guru.qa.niffler.db;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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
                    String uniqId = StringUtils.substringBefore(jdbcUrl, "5432/");
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

    public static <T> T xaTransaction(Supplier<T> supplier) {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin();
            T result = supplier.get();
            ut.commit();
            return result;
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);

        }
    }

    public static void xaTransaction(Runnable runnable) {
        UserTransactionImp ut = new UserTransactionImp();
        try {
            ut.begin();
            runnable.run();
            ut.commit();
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);

        }
    }

    public static <T> T transaction(Supplier<T> supplier, String jdbcUrl) {
        Connection connection = null;
        try {
            connection = Databases.connection(jdbcUrl);
            connection.setAutoCommit(false);
            T result = supplier.get();
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);

        }
    }

    public static void transaction(Runnable runnable, String jdbcUrl) {
        transaction(() -> {
            runnable.run();
            return null;
        }, jdbcUrl);
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
