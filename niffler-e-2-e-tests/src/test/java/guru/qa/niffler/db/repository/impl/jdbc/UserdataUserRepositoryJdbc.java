package guru.qa.niffler.db.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.dao.UserdataFriendshipDao;
import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.jdbc.UserdataFriendshipDaoJdbc;
import guru.qa.niffler.db.dao.impl.jdbc.UserdataUserDaoJdbc;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final String JDBC_URL = Config.getInstance().userdataJdbcUrl();
    private final UserdataUserDao userDao = new UserdataUserDaoJdbc(JDBC_URL);
    private final UserdataFriendshipDao friendshipDao = new UserdataFriendshipDaoJdbc(JDBC_URL);

    @Override
    public @Nonnull UserdataUserEntity create(UserdataUserEntity entity) {
        return userDao.create(entity);
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findById(UUID id) {
        return userDao.findById(id)
            .map(this::addFriendship);
    }

    private @Nonnull UserdataUserEntity addFriendship(UserdataUserEntity entity) {
        UUID userId = entity.getId();
        List<UserdataFriendshipEntity> friendships = friendshipDao.findByUserId(userId);
        for (UserdataFriendshipEntity friendship : friendships) {
            if (userId.equals(friendship.getRequester().getId())) {
                entity.getFriendshipRequests().add(friendship);
            } else {
                entity.getFriendshipAddressees().add(friendship);
            }
        }
        return entity;
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findByUsername(String username) {
        return userDao.findByUsername(username)
            .map(this::addFriendship);
    }

    @Override
    public @Nonnull UserdataUserEntity update(UserdataUserEntity entity) {
        return userDao.update(entity);
    }

    @Override
    public boolean delete(UserdataUserEntity entity) {
        friendshipDao.deleteByUserId(entity.getId());
        return userDao.delete(entity);
    }

    @Override
    public List<UserdataUserEntity> findAll() {
        List<UserdataUserEntity> all = userDao.findAll();
        all.forEach(this::addFriendship);
        return all;
    }

    @Override
    public @Nonnull UserdataFriendshipEntity createFriendship(UserdataUserEntity requester,
                                                     UserdataUserEntity addressee,
                                                     FriendshipStatus status) {
        UserdataFriendshipEntity friendship = new UserdataFriendshipEntity();
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(status);
        friendship.setCreatedDate(new Date());
        return friendshipDao.create(friendship);
    }

}
