package guru.qa.niffler.data.dao.interfaces;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {

    AuthUserEntity create(AuthUserEntity authUserEntity);

    Optional<AuthUserEntity> findById(UUID uuid);

    List<AuthUserEntity> findAll();
}