package guru.qa.niffler.db.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.jpa.EntityManagers;
import guru.qa.niffler.db.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    public final EntityManager entityManager = EntityManagers.em(CFG.authJdbcUrl());

    public AuthUserRepositoryHibernate() {
        System.out.println("constructor AuthUserRepositoryHibernate");
    }


    @Override
    public AuthUserEntity create(AuthUserEntity entity) {
        entityManager.joinTransaction();
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity entity) {
        entityManager.joinTransaction();
        return entityManager.merge(entity);
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        String sql = "SELECT u FROM AuthUserEntity u WHERE u.username =: username";
        try {
            AuthUserEntity user = entityManager.createQuery(sql, AuthUserEntity.class)
                .setParameter("username", username)
                .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(AuthUserEntity entity) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        return true;
    }

    @Override
    public List<AuthUserEntity> findAll() {
        String sql = "SELECT u FROM AuthUserEntity u";
        return entityManager.createQuery(sql, AuthUserEntity.class)
            .getResultList();
    }

}
