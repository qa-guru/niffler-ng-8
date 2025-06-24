package guru.qa.niffler.db.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.jpa.EntityManagers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class AbstractRepositoryHibernate {

    protected static final Config CFG = Config.getInstance();
    protected final EntityManager entityManager;

    public AbstractRepositoryHibernate(String jdbcUrl) {
        this.entityManager = EntityManagers.em(jdbcUrl);
    }

    protected <T> @Nonnull T create(T entity) {
        entityManager.joinTransaction();
        entityManager.persist(entity);
        return entity;
    }

    public <T> @Nonnull T update(T entity) {
        entityManager.joinTransaction();
        return entityManager.merge(entity);
    }

    public <T> @Nonnull Optional<T> findByIdOpt(Class<T> entityClass, @Nullable Object id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    public <T> boolean delete(Class<T> entityClass, @Nullable Object id) {
        Optional<T> entityOpt = findByIdOpt(entityClass, id);
        if (entityOpt.isPresent()) {
            entityManager.remove(entityOpt.get());
            return true;
        }
        return false;
    }

    protected <T> @Nonnull Optional<T> findSingleResultOpt(Class<T> tClass, String sql, Object... params) {
        try {
            TypedQuery<T> query = entityManager.createQuery(sql, tClass);
            setParameters(query, params);
            T singleResult = query.getSingleResult();
            return Optional.of(singleResult);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    protected <T> @Nullable List<T> findResultList(Class<T> tClass, String sql, Object... params) {
        TypedQuery<T> query = entityManager.createQuery(sql, tClass);
        setParameters(query, params);
        return query.getResultList();
    }

    private void setParameters(TypedQuery<?> query, Object... params) {
        for (int i = 1; i <= params.length; i++) {
            query.setParameter(i, params[i - 1]);
        }
    }

}
