package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {

    SpendEntity createSpend(SpendEntity entity);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findAllSpendByUsername(String username);

    List<SpendEntity> findAllSpendByCategoryId(UUID categoryId);

    boolean deleteSpend(SpendEntity entity);

}
