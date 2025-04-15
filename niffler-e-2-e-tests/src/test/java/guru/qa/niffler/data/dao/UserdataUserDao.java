package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {
    UserEntity createUser(UserEntity user);

    Optional<UserEntity> findSById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    void delete(UserEntity user);
}
