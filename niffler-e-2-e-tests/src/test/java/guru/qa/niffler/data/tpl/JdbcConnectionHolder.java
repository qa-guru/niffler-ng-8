package guru.qa.niffler.data.tpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcConnectionHolder implements AutoCloseable {

    private final DataSource dataSource;
    private final Map<Long, Connection> threadConnections = new ConcurrentHashMap<>();

    public JdbcConnectionHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection connection(){
        return threadConnections.computeIfAbsent(
                Thread.currentThread().threadId(),
                key ->
                {
                    try {
                        return dataSource.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Override
    public void close() {
        //Когда захотим закрыть connection из потока,
        //Достанем по ID этого потока, но нужно делать не get потока, а remove
        //Посткольку, если мы просто закроем поток, connection останется в Map
        Optional.ofNullable(threadConnections.remove(Thread.currentThread().threadId()))
                .ifPresent(connection -> {
                    try {
                        if (!connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        //NOP
                    }
                });
    }

    public void closeAllConnections() {
        threadConnections.values().forEach(connection -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }

            } catch (SQLException e) {
                //NOP
            }
        });
    }
}
