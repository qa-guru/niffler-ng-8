package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    UserEntity update(UserEntity user);

    void addInvitation(UserEntity requester, UserEntity addressee, FriendshipStatus status);

    void remove(UserEntity user);
}