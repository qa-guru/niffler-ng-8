package guru.qa.niffler.data.dao.interfaces;

import guru.qa.niffler.data.entity.user.AuthAuthorityEntity;

import java.util.List;

public interface AuthAuthorityDao {

    AuthAuthorityEntity create(AuthAuthorityEntity... authority);

    List<AuthAuthorityEntity> findAll();
}