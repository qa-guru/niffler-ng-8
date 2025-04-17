package guru.qa.niffler.deprecated;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import guru.qa.niffler.db.tpl.Connections;
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

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

@Deprecated(forRemoval = true)
public class Databases {

    private Databases() {
    }

    private static final int TRANSACTION_NONE_SET = -1;
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

    public static DataSource dataSource(String jdbcUrl) {
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

    public <T> T xaTransaction(Supplier<T> supplier, String... jdbcUrls) {
        return xaTransaction(TRANSACTION_NONE_SET, supplier, jdbcUrls);
    }

    public <T> T xaTransaction(Integer transactionIsolation,
                               Supplier<T> supplier,
                               String... jdbcUrls) {
        UserTransaction ut = new UserTransactionImp();
        Map<String, Integer> previousIsolationByUrl = Map.of();
        try {
            ut.begin();
            if (transactionIsolation != TRANSACTION_NONE_SET) {
                previousIsolationByUrl = setTransactionIsolationAndGetPrevious(transactionIsolation, jdbcUrls);
            }
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

        } finally {
            setPreviousTransactionIsolation(previousIsolationByUrl);
        }
    }

    public void xaTransaction(Runnable runnable, String... jdbcUrls) {
        xaTransaction(TRANSACTION_NONE_SET, runnable, jdbcUrls);
    }

    public void xaTransaction(Integer transactionIsolation, Runnable runnable, String... jdbcUrls) {
        UserTransactionImp ut = new UserTransactionImp();
        Map<String, Integer> previousIsolationByUrl = Map.of();
        try {
            ut.begin();
            if (transactionIsolation != TRANSACTION_NONE_SET) {
                previousIsolationByUrl = setTransactionIsolationAndGetPrevious(transactionIsolation, jdbcUrls);
            }
            runnable.run();
            ut.commit();
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            setPreviousTransactionIsolation(previousIsolationByUrl);
        }
    }

    public <T> T transaction(Supplier<T> supplier,
                             String jdbcUrl) {
        return transaction(TRANSACTION_NONE_SET, supplier, jdbcUrl);
    }

    public <T> T transaction(Integer transactionIsolation,
                             Supplier<T> supplier,
                             String jdbcUrl) {
        Connection connection = null;
        int previousIsolation = TRANSACTION_READ_COMMITTED;
        try {
            connection = Connections.holder(jdbcUrl).connection();
            if (transactionIsolation != TRANSACTION_NONE_SET) {
                previousIsolation = connection.getTransactionIsolation();
                connection.setTransactionIsolation(transactionIsolation);
            }
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
        } finally {
            try {
                if (connection != null && transactionIsolation != TRANSACTION_NONE_SET) {
                    connection.setTransactionIsolation(previousIsolation);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void transaction(Runnable runnable,
                            String jdbcUrl) {
        transaction(TRANSACTION_NONE_SET, runnable, jdbcUrl);
    }

    public void transaction(Integer transactionIsolation,
                            Runnable runnable,
                            String jdbcUrl) {
        transaction(
                transactionIsolation,
                () -> {
                    runnable.run();
                    return null;
                },
                jdbcUrl
        );
    }

    private void setPreviousTransactionIsolation(Map<String, Integer> previousIsolationByUrl) {
        for (Map.Entry<String, Integer> isolationByUrl : previousIsolationByUrl.entrySet()) {
            try {
                Connection connection = Connections.holder(isolationByUrl.getKey()).connection();
                connection.setTransactionIsolation(isolationByUrl.getValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, Integer> setTransactionIsolationAndGetPrevious(Integer transactionIsolation, String... jdbcUrls) throws SQLException {
        Map<String, Integer> previousIsolationByUrl = new HashMap<>();
        for (String url : jdbcUrls) {
            Connection connection = Connections.holder(url).connection();
            previousIsolationByUrl.put(url, connection.getTransactionIsolation());
            connection.setTransactionIsolation(transactionIsolation);
        }
        return previousIsolationByUrl;
    }

    public static void closeTransactionManager() {
        utm.close();
    }

}
