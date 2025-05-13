package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.user.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID uuid);

    void addIncomeInvitation(UserEntity requester, UserEntity addressee);

    void addOutcomeInvitation(UserEntity requester, UserEntity addressee);

    void addFriend(UserEntity requester, UserEntity addressee);

    void deleteFriendshipForUser(UserEntity user);

    void deleteUser(UserEntity user);
}