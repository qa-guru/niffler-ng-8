package guru.qa.niffler.db.tpl;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class XaTransactionTemplate {

    private static final int TRANSACTION_NONE_SET = -1;

    private final JdbcConnectionHolders holders;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);
    private static final UserTransactionManager utm;

    static {
        utm = new UserTransactionManager();
        try {
            utm.init();
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    public XaTransactionTemplate(String... jdbcUrls) {
        this.holders = Connections.holders(jdbcUrls);
    }

    public XaTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public <T> T execute(Supplier<T> supplier, String... jdbcUrls) {
        return execute(TRANSACTION_NONE_SET, supplier, jdbcUrls);
    }

    public <T> T execute(Integer transactionIsolation,
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
            if (closeAfterAction.get()) {
                holders.close();
            }
        }
    }

    public void execute(Runnable runnable, String... jdbcUrls) {
        execute(TRANSACTION_NONE_SET, runnable, jdbcUrls);
    }

    public void execute(Integer transactionIsolation, Runnable runnable, String... jdbcUrls) {
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
            if (closeAfterAction.get()) {
                holders.close();
            }
        }
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
