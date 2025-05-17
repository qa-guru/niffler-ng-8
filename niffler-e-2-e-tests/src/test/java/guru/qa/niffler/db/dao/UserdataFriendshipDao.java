package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;

import java.util.List;
import java.util.UUID;

public interface UserdataFriendshipDao {

    UserdataFriendshipEntity create(UserdataFriendshipEntity entity);

    List<UserdataFriendshipEntity> findByUserId(UUID requesterId);

    boolean deleteByUserId(UUID entity);

}
