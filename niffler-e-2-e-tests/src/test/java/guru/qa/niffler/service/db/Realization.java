package guru.qa.niffler.service.db;


import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.CurrencyRepository;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.CurrencyRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserDataRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.SpendRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserDataRepositorySpringJdbc;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Realization {

    JDBC(
            new AuthUserRepositoryJdbc(),
            new UserDataRepositoryJdbc(),
            new SpendRepositoryJdbc(),
            null
    ),
    SPRING(
            new AuthUserRepositorySpringJdbc(),
            new UserDataRepositorySpringJdbc(),
            new SpendRepositorySpringJdbc(),
            null
    ),
    HIBERNATE(
            new AuthUserRepositoryHibernate(),
            new UserdataUserRepositoryHibernate(),
            new SpendRepositoryHibernate(),
            new CurrencyRepositoryHibernate()
    );
    private final AuthUserRepository authUserRepository;
    private final UserdataUserRepository udUserRepository;
    private final SpendRepository spendRepository;
    private final CurrencyRepository currencyRepository;

}
