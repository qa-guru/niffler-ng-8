package guru.qa.niffler.data;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import guru.qa.niffler.model.TransactionIsolation;
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
import java.util.function.Consumer;
import java.util.function.Function;

public class DataBases {
    private DataBases() {
    }

    public static final Map<String, DataSource> datasource = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, Connection>> threadConnections = new ConcurrentHashMap<>();

    public record XaFunction<T>(Function<Connection, T> function, String jdbcUrl) {
    }

    public record XaConsumer<T>(Consumer<Connection> function, String jdbcUrl) {
    }

    public static <T> T transaction(Function<Connection, T> function, String jdbcUrl, TransactionIsolation transactionIsolation) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);
            connection.setTransactionIsolation(transactionIsolation.level);
            connection.setAutoCommit(false);
            T result = function.apply(connection);
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

    public static <T> T xaTransaction(TransactionIsolation transactionIsolation, XaFunction<T>... actions) {
        UserTransaction userTransaction = new UserTransactionImp();
        try {
            userTransaction.begin();
            T result = null;
            for (XaFunction<T> action : actions) {
                Connection connection = connection(action.jdbcUrl);
                connection.setTransactionIsolation(transactionIsolation.ordinal());
                result = action.function.apply(connection(action.jdbcUrl));

            }
            userTransaction.commit();
            return result;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public static void transaction(Consumer<Connection> consumer, String jdbcUrl) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);
            connection.setAutoCommit(false);
            consumer.accept(connection);
            connection.commit();
            connection.setAutoCommit(true);
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

    @SuppressWarnings("unchecked")
    public static void xaTransaction(TransactionIsolation transactionIsolation, XaConsumer... actions) {
        UserTransaction transactionImp = new UserTransactionImp();
        try {
            transactionImp.begin();
            for (XaConsumer action : actions) {
                Connection connection = connection(action.jdbcUrl);
                connection.setTransactionIsolation(transactionIsolation.ordinal());
                action.function.accept(connection(action.jdbcUrl));

            }
            transactionImp.commit();
        } catch (Exception e) {
            try {
                transactionImp.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

//public временно
    public static DataSource dataSource(String jdbcUrl) {
        return datasource.computeIfAbsent(
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    final String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
                    dsBean.setUniqueResourceName(uniqId);
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.put("URL", jdbcUrl);
                    props.put("user", "postgres");
                    props.put("password", "secret");
                    dsBean.setXaProperties(props);
                    dsBean.setMaxPoolSize(10);
                    return dsBean;
                }
        );
    }

    private static Connection connection(String jbcUrl) throws SQLException {
        //Либо достаём мапу, привязанную к текущему потоку
        return DataBases.threadConnections.computeIfAbsent(
                Thread.currentThread().threadId(),
                key -> {
                    try {
                        //Либо кладёт её туда
                        return new HashMap<>(Map.of(
                                jbcUrl,
                                DataBases.dataSource(jbcUrl).getConnection()
                        ));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                //И достав её, ищет по jbcUrl, нужный connection
        ).computeIfAbsent(
                jbcUrl,
                //Либо кладёт его туда
                key -> {
                    try {
                        return DataBases.dataSource(jbcUrl).getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static void closeAllConnections() {
        for (Map<String, Connection> connectionMap : threadConnections.values()) {
            for (Connection connection : connectionMap.values()) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // NOPE
                }
            }
        }
    }
}
