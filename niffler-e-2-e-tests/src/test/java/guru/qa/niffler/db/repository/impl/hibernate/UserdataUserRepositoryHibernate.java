package guru.qa.niffler.db.repository.impl.hibernate;

import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryHibernate extends AbstractRepositoryHibernate implements UserdataUserRepository {

    private static final Class<UserdataUserEntity> USER_CLASS = UserdataUserEntity.class;

    public UserdataUserRepositoryHibernate() {
        super(CFG.userdataJdbcUrl());
    }

    @Override
    public @Nonnull UserdataUserEntity create(UserdataUserEntity entity) {
        return super.create(entity);
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findById(UUID id) {
        return findByIdOpt(USER_CLASS, id);
    }

    @Override
    public @Nonnull UserdataUserEntity update(UserdataUserEntity entity) {
        return super.update(entity);
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findByUsername(String username) {
        String sql = "SELECT u FROM UserdataUserEntity u WHERE u.username = ?1";
        return findSingleResultOpt(USER_CLASS, sql, username);
    }

    @Override
    public boolean delete(UserdataUserEntity entity) {
        return delete(UserdataUserEntity.class, entity.getId());
    }

    @Override
    public @Nullable List<UserdataUserEntity> findAll() {
        String sql = "SELECT u FROM UserdataUserEntity u";
        return findResultList(USER_CLASS, sql);
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
        return create(friendship);
    }

    @Override
    public void addFriend(UserdataUserEntity user, UserdataUserEntity otherUser) {
        user.addFriends(FriendshipStatus.ACCEPTED, otherUser);
        otherUser.addFriends(FriendshipStatus.ACCEPTED, user);
        update(user);
        update(otherUser);
    }

    @Override
    public void addIncomeInvitation(UserdataUserEntity targetUser, UserdataUserEntity requester) {
        entityManager.joinTransaction();
        targetUser.addInvitations(requester);
        update(targetUser);
    }

    @Override
    public void addOutcomeInvitation(UserdataUserEntity targetUser, UserdataUserEntity addressee) {
        targetUser.addFriends(FriendshipStatus.PENDING, addressee);
        update(targetUser);
    }

}
