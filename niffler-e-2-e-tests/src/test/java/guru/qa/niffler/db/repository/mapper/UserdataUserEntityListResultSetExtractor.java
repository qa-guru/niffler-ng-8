package guru.qa.niffler.db.repository.mapper;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserdataUserEntityListResultSetExtractor implements ResultSetExtractor<List<UserdataUserEntity>> {

    public static final UserdataUserEntityListResultSetExtractor INSTANCE = new UserdataUserEntityListResultSetExtractor();

    private UserdataUserEntityListResultSetExtractor() {
    }

    @Override
    public List<UserdataUserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, UserdataUserEntity> userMap = new HashMap<>();

        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);
            if (userId == null) continue;

            UserdataUserEntity user = userMap.computeIfAbsent(userId, id -> {
                try {
                    return mapUser(rs, "");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            UUID requesterId = rs.getObject("requester_id", UUID.class);
            UUID addresseeId = rs.getObject("addressee_id", UUID.class);

            if (requesterId != null && addresseeId != null) {
                UserdataFriendshipEntity friendship = new UserdataFriendshipEntity();
                friendship.setCreatedDate(rs.getTimestamp("created_date"));
                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));

                if (userId.equals(requesterId)) {
                    friendship.setRequester(user);
                    UserdataUserEntity addressee = mapUser(rs, "addressee_");
                    friendship.setAddressee(addressee);
                    user.getFriendshipRequests().add(friendship);
                } else if (userId.equals(addresseeId)) {
                    friendship.setAddressee(user);
                    UserdataUserEntity requester = mapUser(rs, "requester_");
                    friendship.setRequester(requester);
                    user.getFriendshipAddressees().add(friendship);
                }
            }
        }

        return new ArrayList<>(userMap.values());
    }

    private UserdataUserEntity mapUser(ResultSet rs, String prefix) throws SQLException {
        return new UserdataUserEntity()
                .setId(rs.getObject(prefix + "id", UUID.class))
                .setUsername(rs.getString(prefix + "username"))
                .setCurrency(CurrencyValues.valueOf(rs.getString(prefix + "currency")))
                .setFirstname(rs.getString(prefix + "firstname"))
                .setSurname(rs.getString(prefix + "surname"))
                .setFullname(rs.getString(prefix + "full_name"))
                .setPhoto(rs.getBytes(prefix + "photo"))
                .setPhotoSmall(rs.getBytes(prefix + "photo_small"));
    }

}
