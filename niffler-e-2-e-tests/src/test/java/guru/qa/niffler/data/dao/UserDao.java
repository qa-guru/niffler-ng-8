package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserDao {
    UserEntity create(UserEntity user);
    Optional<UserEntity> findUserById(UUID id);
    Optional<UserEntity> findUserByUsername(String username);
    void deleteUsername(UserEntity user);
}
