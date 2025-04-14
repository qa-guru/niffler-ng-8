package guru.qa.niffler.db.service;

import com.atomikos.icatch.jta.UserTransactionImp;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.Databases;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class AbstractDbClient {

    protected final static Config CFG = Config.getInstance();
    private static final int TRANSACTION_NONE_SET = -1;

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
            connection = Databases.connection(jdbcUrl);
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
                Connection connection = Databases.connection(isolationByUrl.getKey());
                connection.setTransactionIsolation(isolationByUrl.getValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, Integer> setTransactionIsolationAndGetPrevious(Integer transactionIsolation, String... jdbcUrls) throws SQLException {
        Map<String, Integer> previousIsolationByUrl = new HashMap<>();
        for (String url : jdbcUrls) {
            Connection connection = Databases.connection(url);
            previousIsolationByUrl.put(url, connection.getTransactionIsolation());
            connection.setTransactionIsolation(transactionIsolation);
        }
        return previousIsolationByUrl;
    }

}
