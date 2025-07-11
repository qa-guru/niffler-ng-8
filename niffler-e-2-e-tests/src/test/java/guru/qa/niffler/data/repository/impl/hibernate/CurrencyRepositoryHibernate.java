package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.currency.CurrencyEntity;
import guru.qa.niffler.data.repository.CurrencyRepository;
import jakarta.persistence.EntityManager;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class CurrencyRepositoryHibernate implements CurrencyRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.currencyJdbcUrl());


    @Override
    public List<CurrencyEntity> getAll() {
        return entityManager.createQuery(
                "SELECT c FROM CurrencyEntity c",
                CurrencyEntity.class
        )
                .getResultList();
    }
}
