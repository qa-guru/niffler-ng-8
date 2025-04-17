package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAuthorityDao {

    AuthAuthorityEntity create(AuthAuthorityEntity entity);

    AuthAuthorityEntity update(AuthAuthorityEntity entity);

    Optional<AuthAuthorityEntity> findById(UUID id);

    List<AuthAuthorityEntity> findByUserId(UUID userId);

    boolean delete(AuthAuthorityEntity entity);

    List<AuthAuthorityEntity> findAll();

}
