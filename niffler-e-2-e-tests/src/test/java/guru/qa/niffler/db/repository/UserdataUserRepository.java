package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {

    enum RelationsType {
        INCOME,
        OUTCOME,
        ADD_FRIEND
    }

    UserdataUserEntity create(UserdataUserEntity entity);

    Optional<UserdataUserEntity> findById(UUID id);

    Optional<UserdataUserEntity> findByUsername(String username);

    boolean delete(UserdataUserEntity entity);

    List<UserdataUserEntity> findAll();

    default void addIncomeInvitation(UserdataUserEntity user, UserdataUserEntity requester) {
        createFriendship(user, requester, RelationsType.INCOME);
    }

    default void addOutcomeInvitation(UserdataUserEntity user, UserdataUserEntity addressee) {
        createFriendship(user, addressee, RelationsType.OUTCOME);
    }

    default void addFriend(UserdataUserEntity user, UserdataUserEntity otherUser) {
        createFriendship(user, otherUser, RelationsType.ADD_FRIEND);
    }

    default void createFriendship(UserdataUserEntity user, UserdataUserEntity otherUser, RelationsType type) {
        if (type == RelationsType.INCOME) {
            UserdataFriendshipEntity friendship = createFriendship(otherUser, user, FriendshipStatus.PENDING);
            user.getFriendshipAddressees().add(friendship);
            otherUser.getFriendshipRequests().add(friendship);
        } else if (type == RelationsType.OUTCOME) {
            UserdataFriendshipEntity friendship = createFriendship(user, otherUser, FriendshipStatus.PENDING);
            user.getFriendshipRequests().add(friendship);
            otherUser.getFriendshipAddressees().add(friendship);
        } else if (type == RelationsType.ADD_FRIEND) {
            UserdataFriendshipEntity friendshipOutcome = createFriendship(user, otherUser, FriendshipStatus.ACCEPTED);
            UserdataFriendshipEntity friendshipIncome = createFriendship(otherUser, user, FriendshipStatus.ACCEPTED);

            user.getFriendshipRequests().add(friendshipOutcome);
            otherUser.getFriendshipAddressees().add(friendshipOutcome);

            otherUser.getFriendshipRequests().add(friendshipIncome);
            user.getFriendshipAddressees().add(friendshipIncome);
        }
    }

    UserdataFriendshipEntity createFriendship(UserdataUserEntity requester, UserdataUserEntity addressee, FriendshipStatus status);

}
