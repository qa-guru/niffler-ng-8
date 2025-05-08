package guru.qa.niffler.db.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.jpa.EntityManagers;
import guru.qa.niffler.db.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    public final EntityManager entityManager = EntityManagers.em(CFG.userdataJdbcUrl());


    @Override
    public UserdataUserEntity create(UserdataUserEntity entity) {
        entityManager.joinTransaction();
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Optional<UserdataUserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserdataUserEntity.class, id));
    }

    @Override
    public UserdataUserEntity update(UserdataUserEntity entity) {
        entityManager.joinTransaction();
        return entityManager.merge(entity);
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
        String sql = "SELECT u FROM UserdataUserEntity u WHERE u.username =: username";
        try {
            UserdataUserEntity user = entityManager.createQuery(sql, UserdataUserEntity.class)
                .setParameter("username", username)
                .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(UserdataUserEntity entity) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        return true;
    }

    @Override
    public List<UserdataUserEntity> findAll() {
        String sql = "SELECT u FROM UserdataUserEntity u";
        return entityManager.createQuery(sql, UserdataUserEntity.class)
            .getResultList();
    }

    @Override
    public UserdataFriendshipEntity createFriendship(UserdataUserEntity requester,
                                                     UserdataUserEntity addressee,
                                                     FriendshipStatus status) {
        entityManager.joinTransaction();
        UserdataFriendshipEntity friendship = new UserdataFriendshipEntity();
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(status);
        friendship.setCreatedDate(new Date());
        entityManager.persist(friendship);
        return friendship;
    }

    @Override
    public void addFriend(UserdataUserEntity requester, UserdataUserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }

    @Override
    public void addIncomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee) {
        entityManager.joinTransaction();
        addressee.addFriends(FriendshipStatus.PENDING, requester);
    }

    @Override
    public void addOutcomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

}
