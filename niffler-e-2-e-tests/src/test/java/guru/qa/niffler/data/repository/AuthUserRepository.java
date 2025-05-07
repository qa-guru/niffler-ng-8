package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    AuthUserEntity create(AuthUserEntity authUserEntity);

    Optional<AuthUserEntity> findById(UUID uuid);

    List<AuthUserEntity> findAll();

    void deleteAuthority(UserEntity uuid);

    void deleteUser(UserEntity uuid);
}