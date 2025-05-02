package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UdUserEntityExtractor implements ResultSetExtractor<UserEntity> {
    public static final UdUserEntityExtractor instance = new UdUserEntityExtractor();

    private UdUserEntityExtractor() {
    }

    @Override
    public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, UserEntity> userMap = new ConcurrentHashMap<>();
        UUID userId = null;
        while (rs.next()) {
            userId = rs.getObject("id", UUID.class);
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
                    throw new DataAccessException("Error creating user entity", e) {
                    };
                }
                return newUser;
            });
            FriendshipEntity friendship = new FriendshipEntity();

            friendship.setCreatedDate(rs.getDate("created_date"));
            friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
            UUID requesterId = rs.getObject("requester_id",UUID.class);
            UUID addresseeId = rs.getObject("addressee_id",UUID.class);
            if(requesterId.equals(userId)){
                friendship.setRequester(user);
                UserEntity addressee = new UserEntity();
                addressee.setId(addresseeId);
                friendship.setAddressee(addressee);
                user.getFriendshipRequests().add(friendship);
            } else {
                UserEntity requester = new UserEntity();
                requester.setId(requesterId);
                friendship.setRequester(requester);
                friendship.setAddressee(user);
                user.getFriendshipAddressees().add(friendship);
            }
        }
        return userMap.get(userId);
    }
}