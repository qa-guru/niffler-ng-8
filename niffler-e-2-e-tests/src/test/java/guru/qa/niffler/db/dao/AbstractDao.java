package guru.qa.niffler.db.dao;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.Databases;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<E> {

    public static final Config CFG = Config.getInstance();
    public final String jdbcUrl;

    public AbstractDao(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    protected E executeQuery(String sql, Object... params) {
        try (Connection cn = Databases.connection(jdbcUrl)) {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                fillPrepareStatement(params, ps);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSet(rs);
                    } else {
                        throw new SQLException("Не найден результаты вызова");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean executeUpdateToBoolean(String sql, Object... params) {
        try (Connection cn = Databases.connection(jdbcUrl)) {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                fillPrepareStatement(params, ps);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected Optional<E> executeQueryToOptional(String sql, Object... params) {
        try (Connection connection = Databases.connection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<E> executeQueryToList(String sql, Object... params) {
        try (Connection connection = Databases.connection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<E> entities = new ArrayList<>();
                while (rs.next()) {
                    entities.add(mapResultSet(rs));
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract E mapResultSet(ResultSet rs) throws SQLException;

    private void inspectResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        System.out.println("=== ResultSet Structure ===");
        for (int i = 1; i <= colCount; i++) {
            System.out.printf("%d: %s (%s, precision: %d, scale: %d)%n",
                    i,
                    meta.getColumnName(i),
                    meta.getColumnTypeName(i),
                    meta.getPrecision(i),
                    meta.getScale(i));
        }

        System.out.println("=== Data ===");
        while (rs.next()) {
            for (int i = 1; i <= colCount; i++) {
                Object value = rs.getObject(i);
                System.out.printf("%s: %s | ",
                        meta.getColumnName(i),
                        value != null ? value : "NULL");
            }
            System.out.println();
        }
    }

    private void fillPrepareStatement(Object[] params, PreparedStatement ps) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

}
