package guru.qa.niffler.data.tpl;

import guru.qa.niffler.model.TransactionIsolation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class JdbcTransactionTemplate {

    private final JdbcConnectionHolder holder;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public JdbcTransactionTemplate(String jdbcUrl) {
        this.holder = Connections.holder(jdbcUrl);
    }

    public JdbcTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public <T> T execute(Supplier<T> action, TransactionIsolation transactionIsolation) {
        Connection connection = null;
        try {
            connection = holder.connection();
            connection.setTransactionIsolation(transactionIsolation.level);
            connection.setAutoCommit(false);
            T result = action.get();
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
            if (closeAfterAction.get()) {
                try {
                    holder.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void executeVoid(Runnable action, TransactionIsolation transactionIsolation) {
        Connection connection = null;
        try {
            connection = holder.connection();
            connection.setTransactionIsolation(transactionIsolation.level);
            connection.setAutoCommit(false);
            action.run();
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
        } finally {
            if (closeAfterAction.get()) {
                try {
                    holder.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
