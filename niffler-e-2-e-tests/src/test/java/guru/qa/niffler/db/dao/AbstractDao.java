package guru.qa.niffler.db.dao;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.Databases;
import org.springframework.util.function.ThrowingFunction;

import java.sql.*;
import java.util.Optional;

public abstract class AbstractDao<E, ID> {

    public static final Config CFG = Config.getInstance();
    public final String jdbcUrl;

    public AbstractDao(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    protected <R> R create(E entity, String sql, ThrowingFunction<ResultSet, R> genKeysMapper) {
        try (Connection cn = Databases.connection(jdbcUrl)) {
            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                fillCreatePrepareStatement(entity, ps);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return genKeysMapper.apply(rs);
                    } else {
                        throw new SQLException("Не найден результат в ResultSet");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void fillCreatePrepareStatement(E entity, PreparedStatement ps) throws SQLException;

    protected Optional<E> findById(ID id, String sql) {
        try (Connection connection = Databases.connection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
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

    protected abstract E mapResultSet(ResultSet rs) throws SQLException;

}
