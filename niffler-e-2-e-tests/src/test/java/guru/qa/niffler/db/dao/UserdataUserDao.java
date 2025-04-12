package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {

    UserdataUserEntity createUserdata(UserdataUserEntity entity);

    Optional<UserdataUserEntity> findUserdataById(UUID id);

    Optional<UserdataUserEntity> findUserdataByUsername(String username);

    boolean deleteUserdata(UserdataUserEntity entity);

}
