package guru.qa.niffler.db.tpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class JdbcTransactionTemplate {

    private static final int TRANSACTION_NONE_SET = -1;

    private final JdbcConnectionHolder holder;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public JdbcTransactionTemplate(String jdbcUrl) {
        this.holder = Connections.holder(jdbcUrl);
    }

    public JdbcTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public <T> T execute(Supplier<T> supplier) {
        return execute(TRANSACTION_NONE_SET, supplier);
    }

    public <T> T execute(Integer transactionIsolation,
                         Supplier<T> supplier) {
        Connection connection = null;
        int previousIsolation = TRANSACTION_NONE_SET;
        try {
            connection = holder.connection();
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
                if (connection != null) {
                    if (transactionIsolation != TRANSACTION_NONE_SET) {
                        connection.setTransactionIsolation(previousIsolation);
                    }
                    if (closeAfterAction.get()) {
                        holder.close();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void execute(Runnable runnable) {
        execute(TRANSACTION_NONE_SET, runnable);
    }

    public void execute(Integer transactionIsolation, Runnable runnable) {
        execute(
                transactionIsolation,
                () -> {
                    runnable.run();
                    return null;
                }
        );
    }

}
