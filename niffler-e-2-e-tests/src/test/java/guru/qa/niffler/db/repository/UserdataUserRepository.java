package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {

    UserdataUserEntity create(UserdataUserEntity entity);

    Optional<UserdataUserEntity> findById(UUID id);

    Optional<UserdataUserEntity> findByUsername(String username);

    UserdataUserEntity update(UserdataUserEntity entity);

    boolean delete(UserdataUserEntity entity);

    List<UserdataUserEntity> findAll();

    default void addIncomeInvitation(UserdataUserEntity targetUser, UserdataUserEntity requester) {
        UserdataFriendshipEntity friendship = createFriendship(requester, targetUser, FriendshipStatus.PENDING);
        targetUser.getFriendshipAddressees().add(friendship);
        requester.getFriendshipRequests().add(friendship);
    }

    default void addOutcomeInvitation(UserdataUserEntity targetUser, UserdataUserEntity addressee) {
        UserdataFriendshipEntity friendship = createFriendship(targetUser, addressee, FriendshipStatus.PENDING);
        targetUser.getFriendshipRequests().add(friendship);
        addressee.getFriendshipAddressees().add(friendship);
    }

    default void addFriend(UserdataUserEntity user, UserdataUserEntity otherUser) {
        UserdataFriendshipEntity friendshipOutcome = createFriendship(user, otherUser, FriendshipStatus.ACCEPTED);
        UserdataFriendshipEntity friendshipIncome = createFriendship(otherUser, user, FriendshipStatus.ACCEPTED);

        user.getFriendshipRequests().add(friendshipOutcome);
        otherUser.getFriendshipAddressees().add(friendshipOutcome);

        otherUser.getFriendshipRequests().add(friendshipIncome);
        user.getFriendshipAddressees().add(friendshipIncome);
    }

    UserdataFriendshipEntity createFriendship(UserdataUserEntity requester, UserdataUserEntity addressee, FriendshipStatus status);

    default List<UserdataFriendshipEntity> selectFriendships(@Nullable UUID requesterId,
                                                             @Nullable UUID addresseeId,
                                                             @Nullable FriendshipStatus status) {
        throw new UnsupportedOperationException("Метод не реализован");
    }
}
