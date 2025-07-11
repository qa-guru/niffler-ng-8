package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.entity.currency.CurrencyValues;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UdUserEntityListExtractor implements ResultSetExtractor<List<UserEntity>> {

    public static final UdUserEntityListExtractor instance = new UdUserEntityListExtractor();

    private UdUserEntityListExtractor() {
    }
    @Override
    public List<UserEntity> extractData(ResultSet rs) throws SQLException {
        Map<UUID, UserEntity> userMap = new LinkedHashMap<>();

        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);
            UserEntity user = userMap.computeIfAbsent(userId, id -> {
                UserEntity newUser = new UserEntity();
                try {
                    newUser.setId(id);
                    newUser.setUsername(rs.getString("username"));
                    newUser.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    newUser.setFirstname(rs.getString("firstname"));
                    newUser.setSurname(rs.getString("surname"));
                    newUser.setFullname(rs.getString("full_name"));
                    newUser.setPhoto(rs.getBytes("photo"));
                    newUser.setPhotoSmall(rs.getBytes("photo_small"));
                } catch (SQLException e) {
                    throw new RuntimeException("Error creating user", e);
                }
                return newUser;
            });

            if (rs.getString("status") != null) {
                FriendshipEntity friendship = new FriendshipEntity();
                friendship.setCreatedDate(rs.getDate("created_date"));
                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));

                UUID requesterId = rs.getObject("requester_id", UUID.class);
                UUID addresseeId = rs.getObject("addressee_id", UUID.class);

                if (requesterId.equals(userId)) {
                    friendship.setRequester(user);
                    UserEntity addressee = new UserEntity();
                    addressee.setId(addresseeId);
                    friendship.setAddressee(addressee);
                    user.getFriendshipRequests().add(friendship);
                } else {
                    friendship.setAddressee(user);
                    UserEntity requester = new UserEntity();
                    requester.setId(requesterId);
                    friendship.setRequester(requester);
                    user.getFriendshipAddressees().add(friendship);
                }
            }
        }

        return new ArrayList<>(userMap.values());
    }
}

