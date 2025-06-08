package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);
    Optional<SpendEntity> findSpendById(UUID id);
    Optional<SpendEntity> findSpendByUsernameAndCategoryName(String username);
    List<SpendEntity> findAllSpendByUsername(String username);
    void deleteSpend(SpendEntity spend);
}
