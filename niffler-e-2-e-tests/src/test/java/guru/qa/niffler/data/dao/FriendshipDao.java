package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;

import java.util.List;
import java.util.UUID;

public interface FriendshipDao {

    void create(FriendshipEntity... friendships);
    List<FriendshipEntity> findAllByUserId(UUID userId);
    List<FriendshipEntity> findAllByFriendId(UUID friendId);
    void updateStatus(UUID id, FriendshipStatus status);
    void deleteAll();
}