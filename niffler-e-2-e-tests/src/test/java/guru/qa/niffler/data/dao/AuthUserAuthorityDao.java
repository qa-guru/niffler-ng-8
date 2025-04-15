package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.user.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserAuthorityDao {
    AuthorityEntity create(AuthorityEntity authority);
    Optional<AuthorityEntity> findById(UUID id);
    List<AuthorityEntity> findByUserId(UUID userId);
    void delete(AuthorityEntity authority);
}