package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {

    SpendEntity create(SpendEntity entity);

    Optional<SpendEntity> findById(UUID id);

    List<SpendEntity> findAllByUsername(String username);

    List<SpendEntity> findAllByCategoryId(UUID categoryId);

    boolean delete(SpendEntity entity);

    List<SpendEntity> findAll();

}
