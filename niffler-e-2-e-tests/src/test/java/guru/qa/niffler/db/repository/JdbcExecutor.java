package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.tpl.Connections;
import org.springframework.util.function.ThrowingFunction;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class JdbcExecutor<E> {

    private final String jdbcUrl;
    private final ThrowingFunction<ResultSet, E> mapper;

    public JdbcExecutor(String jdbcUrl, ThrowingFunction<ResultSet, E> mapper) {
        this.jdbcUrl = jdbcUrl;
        this.mapper = mapper;
    }

    public @Nonnull E executeQuery(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.apply(rs);
                } else {
                    throw new SQLException("Не найден результаты вызова");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean executeUpdateToBoolean(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nonnull Optional<E> executeQueryToOptional(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.apply(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nonnull List<E> executeQueryToList(String sql, Object... params) {
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            fillPrepareStatement(params, ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<E> entities = new ArrayList<>();
                while (rs.next()) {
                    entities.add(mapper.apply(rs));
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillPrepareStatement(Object[] params, PreparedStatement ps) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    public @Nonnull Connection getConnection() {
        return Connections.holder(jdbcUrl).connection();
    }

}
