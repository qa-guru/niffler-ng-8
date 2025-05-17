package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.api.model.Authority;
import guru.qa.niffler.db.dao.AuthAuthorityDao;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoJdbc extends AbstractDao<AuthAuthorityEntity> implements AuthAuthorityDao {

    public AuthAuthorityDaoJdbc(String jdbcUrl) {
        super(jdbcUrl);
    }

    @Override
    public AuthAuthorityEntity create(AuthAuthorityEntity entity) {
        String sql = "INSERT INTO authority (user_id, authority) " +
                "VALUES(?, ?) RETURNING *";
        return executeQuery(sql, entity.getUser().getId(), entity.getAuthority().name());
    }

    @Override
    public AuthAuthorityEntity update(AuthAuthorityEntity entity) {
        String sql = "UPDATE authority SET user_id = ?, authority = ? WHERE id = ? RETURNING *";
        return executeQuery(sql, entity.getUser().getId(), entity.getAuthority().name(), entity.getId());
    }

    @Override
    public Optional<AuthAuthorityEntity> findById(UUID id) {
        String sql = "SELECT * FROM authority WHERE id = ?";
        return executeQueryToOptional(sql, id);
    }

    @Override
    public List<AuthAuthorityEntity> findByUserId(UUID userId) {
        String sql = "SELECT * FROM authority WHERE user_id = ?";
        return executeQueryToList(sql, userId);
    }

    @Override
    public boolean delete(AuthAuthorityEntity entity) {
        String sql = "DELETE FROM authority WHERE id = ?";
        return executeUpdateToBoolean(sql, entity.getId());
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        String sql = "SELECT * FROM authority";
        return executeQueryToList(sql);
    }

    @Override
    protected AuthAuthorityEntity mapResultSet(ResultSet rs) throws SQLException {
        return new AuthAuthorityEntity()
                .setId(rs.getObject("id", UUID.class))
                .setAuthority(Authority.valueOf(rs.getString("authority")));
    }

}
