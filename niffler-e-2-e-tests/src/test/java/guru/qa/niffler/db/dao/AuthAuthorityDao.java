package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAuthorityDao {

    AuthAuthorityEntity createAuthAuthority(AuthAuthorityEntity entity);

    AuthAuthorityEntity updateAuthAuthority(AuthAuthorityEntity entity);

    Optional<AuthAuthorityEntity> findAuthAuthorityById(UUID id);

    List<AuthAuthorityEntity> findAuthAuthorityByUserId(UUID userId);

    boolean deleteAuthAuthority(AuthAuthorityEntity entity);

    List<AuthAuthorityEntity> findAllAuthAuthorities();

}
