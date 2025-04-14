package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {

    AuthUserEntity createAuthUser(AuthUserEntity entity);

    AuthUserEntity updateAuthUser(AuthUserEntity entity);

    Optional<AuthUserEntity> findAuthUserById(UUID id);

    Optional<AuthUserEntity> findAuthUserByUsername(String username);

    boolean deleteAuthUser(AuthUserEntity entity);

    List<AuthUserEntity> findAllAuthUsers();

}
