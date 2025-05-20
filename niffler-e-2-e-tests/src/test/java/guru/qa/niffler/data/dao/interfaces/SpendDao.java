package guru.qa.niffler.data.dao.interfaces;

import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.user.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findAllByUsername(String username);

    void deleteSpend(SpendEntity spend);

    void deleteAllSpendsByUsername(String username);

    List<SpendEntity> findAll();
}
