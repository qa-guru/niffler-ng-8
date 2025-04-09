package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.userdata.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    UserEntity createUser(UserEntity entity);

    Optional<UserEntity> findUserById(UUID id);

    Optional<UserEntity> findUserByUsername(String username);

    boolean deleteUser(UserEntity entity);

}
