package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {

    UserdataUserEntity create(UserdataUserEntity entity);

    Optional<UserdataUserEntity> findById(UUID id);

    Optional<UserdataUserEntity> findByUsername(String username);

    boolean delete(UserdataUserEntity entity);

    List<UserdataUserEntity> findAll();

}
