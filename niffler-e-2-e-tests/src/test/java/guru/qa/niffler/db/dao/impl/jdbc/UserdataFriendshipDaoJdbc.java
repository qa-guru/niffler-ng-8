package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.db.dao.UserdataFriendshipDao;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataFriendshipDaoJdbc extends AbstractDao<UserdataFriendshipEntity> implements UserdataFriendshipDao {

    public UserdataFriendshipDaoJdbc(String jdbcUrl) {
        super(jdbcUrl);
    }

    @Override
    public @Nonnull UserdataFriendshipEntity create(UserdataFriendshipEntity entity) {
        String sql = "INSERT INTO friendship (requester_id, addressee_id, created_date, status)  " +
            "VALUES (?, ?, NOW(), ?) RETURNING *";
        return executeQuery(sql, entity.getRequester().getId(), entity.getAddressee().getId(), entity.getStatus().name());
    }

    @Override
    public @Nonnull List<UserdataFriendshipEntity> findByUserId(UUID requesterId) {
        String sql = "SELECT * FROM friendship WHERE requester_id = ? or addressee_id = ?";
        return executeQueryToList(sql, requesterId, requesterId);
    }

    @Override
    public boolean deleteByUserId(UUID id) {
        String sql = "DELETE FROM friendship WHERE requester_id = ? or addressee_id = ?";
        return executeUpdateToBoolean(sql, id, id);
    }

    @Override
    protected @Nonnull UserdataFriendshipEntity mapResultSet(ResultSet rs) throws SQLException {
        return new UserdataFriendshipEntity()
            .setRequester(new UserdataUserEntity().setId(rs.getObject("requester_id", UUID.class)))
            .setAddressee(new UserdataUserEntity().setId(rs.getObject("addressee_id", UUID.class)))
            .setStatus(FriendshipStatus.valueOf(rs.getString("status")))
            .setCreatedDate(rs.getDate("created_date"));
    }

}
