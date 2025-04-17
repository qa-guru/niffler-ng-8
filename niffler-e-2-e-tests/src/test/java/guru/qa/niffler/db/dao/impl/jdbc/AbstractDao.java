package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.db.tpl.Connections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<E> {

    private final String jdbcUrl;

    public AbstractDao(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    protected E executeQuery(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                } else {
                    throw new SQLException("Не найден результаты вызова");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean executeUpdateToBoolean(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected Optional<E> executeQueryToOptional(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
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
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
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

    private void fillPrepareStatement(Object[] params, PreparedStatement ps) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    public Connection getConnection() {
        return Connections.holder(jdbcUrl).connection();
    }

}
