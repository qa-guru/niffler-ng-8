package guru.qa.niffler.data.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {
    @Override
    public CategoryEntity create(SpendEntity spend) {
        return null;
    }

    @Override
    public Optional<CategoryEntity> findSpendById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<CategoryEntity> findSpendByUsernameAndCategoryName(String username) {
        return Optional.empty();
    }

    @Override
    public List<CategoryEntity> findAllSpendByUsername(String username) {
        return List.of();
    }

    @Override
    public void deleteSpend(SpendEntity spend) {

    }
}
