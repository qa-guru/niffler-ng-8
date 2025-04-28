package guru.qa.niffler.data.dao.interfaces;

import guru.qa.niffler.data.entity.user.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID uuid);

    List<UserEntity> findAll();
//
//    void deleteUser(UserEntity user);
}