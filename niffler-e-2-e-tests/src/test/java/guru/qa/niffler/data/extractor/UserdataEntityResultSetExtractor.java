 package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.userdata.FriendShipId;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserdataEntityResultSetExtractor implements ResultSetExtractor<UserEntity> {

    public static final UserdataEntityResultSetExtractor instance = new UserdataEntityResultSetExtractor();

    private UserdataEntityResultSetExtractor(){}

    @Override
    public UserEntity extractData(ResultSet rs) throws SQLException {
        Map<UUID, UserEntity> userMap = new ConcurrentHashMap<>();
        Map<FriendShipId, FriendshipEntity> friendshipCache = new ConcurrentHashMap<>();

        while (rs.next()) {
            UUID userId = rs.getObject("user_id", UUID.class);
            UserEntity user = userMap.computeIfAbsent(userId, id -> {
                try {
                    UserEntity newUser = new UserEntity();
                    newUser.setId(id);
                    newUser.setUsername(rs.getString("username"));
                    newUser.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    newUser.setFirstname(rs.getString("firstname"));
                    newUser.setSurname(rs.getString("surname"));
                    newUser.setPhoto(rs.getBytes("photo"));
                    newUser.setPhotoSmall(rs.getBytes("photo_small"));
                    newUser.setFriendshipRequests(new ArrayList<>());
                    newUser.setFriendshipAddressees(new ArrayList<>());
                    return newUser;
                } catch (SQLException e) {
                    throw new RuntimeException("Error mapping user data", e);
                }
            });

            UUID requesterId = rs.getObject("requester_id", UUID.class);
            UUID addresseeId = rs.getObject("addressee_id", UUID.class);
            String status = rs.getString("status");
            Date createdDate = rs.getDate("created_date");

            if (requesterId != null && addresseeId != null) {
                FriendShipId friendshipId = new FriendShipId();
                friendshipId.setRequester(requesterId);
                friendshipId.setAddressee(addresseeId);

                friendshipCache.computeIfAbsent(friendshipId, key -> {
                    FriendshipEntity friendship = new FriendshipEntity();
                    friendship.setStatus(FriendshipStatus.valueOf(status));
                    friendship.setCreatedDate(createdDate);

                    UserEntity requester = new UserEntity();
                    requester.setId(requesterId);
                    UserEntity addressee = new UserEntity();
                    addressee.setId(addresseeId);

                    friendship.setRequester(requester);
                    friendship.setAddressee(addressee);

                    if (userId.equals(requesterId)) {
                        user.getFriendshipRequests().add(friendship);
                    }
                    if (userId.equals(addresseeId)) {
                        user.getFriendshipAddressees().add(friendship);
                    }
                    return friendship;
                });
            }
        }

        return userMap.isEmpty() ? null : userMap.values().iterator().next();
    }
}
