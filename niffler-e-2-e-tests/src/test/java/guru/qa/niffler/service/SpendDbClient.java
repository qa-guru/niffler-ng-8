package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.*;
import guru.qa.niffler.utils.RandomDataUtils;
import lombok.NonNull;

import java.sql.Connection;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(@NonNull SpendJson spend) {
        return xaTransaction(
                Connection.TRANSACTION_SERIALIZABLE,
                new Databases.XaFunction<>(connection -> {
                    UserDaoJdbc udj = new UserDaoJdbc(connection);
                    udj.findByUsername(spend.username())
                            .ifPresentOrElse(user->{},
                                    () -> {
                                UserJson json = new UserJson(
                                        null,
                                        spend.username(),
                                        RandomDataUtils.randomName(),
                                        RandomDataUtils.randomSurname(),
                                        RandomDataUtils.randomName(),
                                        CurrencyValues.RUB,
                                        null,
                                        null
                                );
                                udj.create(UserEntity.fromJson(json));
                            });
                    return null;
                        },
                        CFG.userdataJdbcUrl()
                ),
                new Databases.XaFunction<>(connection -> {
                    AuthUserDaoJdbc audj = new AuthUserDaoJdbc(connection);
                    audj.findByUsername(spend.username())
                            .ifPresentOrElse(user -> {},
                                    ()->{
                                        AuthUserJson userJson = new AuthUserJson(
                                                null,
                                                spend.username(),
                                                RandomDataUtils.randomPassword(3,12),
                                                true,
                                                true,
                                                true,
                                                true
                                        );
                                        UUID userId = audj
                                                .create(AuthUserEntity.fromJson(userJson))
                                                .getId();
                                        AuthorityDaoJdbc adj = new AuthorityDaoJdbc(connection);

                                        AuthorityJson authorityJsonWrite = new AuthorityJson(
                                                null,
                                                userId,
                                                Authority.write
                                        );

                                        AuthorityJson authorityJsonRead = new AuthorityJson(
                                                null,
                                                userId,
                                                Authority.read
                                        );

                                        adj.create(
                                                AuthorityEntity.fromJson(authorityJsonRead),
                                                AuthorityEntity.fromJson(authorityJsonWrite)
                                        );
                                    });
                    return null;
                },
                        CFG.authJdbcUrl()
                ),
                new Databases.XaFunction<>(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                                .create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            new SpendDaoJdbc(connection).create(spendEntity)
                    );
                },
                CFG.spendJdbcUrl()
                )
        );
    }

    public String createUser(String username){
        return xaTransaction(
                Connection.TRANSACTION_SERIALIZABLE,
                new Databases.XaFunction<>(connection -> {
                    UserJson json = new UserJson(
                            null,
                            username,
                            RandomDataUtils.randomName(),
                            RandomDataUtils.randomSurname(),
                            RandomDataUtils.randomName(),
                            CurrencyValues.RUB,
                            null,
                            null
                    );
                    new UserDaoJdbc(connection)
                            .create(UserEntity.fromJson(json));
                    return null;
                },
                        CFG.userdataJdbcUrl()
                ),
                new Databases.XaFunction<>(connection -> {
                    String password = RandomDataUtils.randomPassword(3,12);
                    AuthUserJson userJson = new AuthUserJson(
                            null,
                            username,
                            RandomDataUtils.randomPassword(3,12),
                            true,
                            true,
                            true,
                            true
                    );
                    UUID userId = new AuthUserDaoJdbc(connection)
                            .create(AuthUserEntity.fromJson(userJson))
                            .getId();
                    AuthorityDaoJdbc adj = new AuthorityDaoJdbc(connection);

                    AuthorityJson authorityJsonWrite = new AuthorityJson(
                            null,
                            userId,
                            Authority.write
                    );

                    AuthorityJson authorityJsonRead = new AuthorityJson(
                            null,
                            userId,
                            Authority.read
                    );

                    adj.create(
                            AuthorityEntity.fromJson(authorityJsonRead),
                            AuthorityEntity.fromJson(authorityJsonWrite)
                    );
                    return password;
                },
                        CFG.authJdbcUrl()
                ));
    }


    public void deleteSpend(@NonNull SpendJson spendJson){
        transaction(connection -> {
                    new SpendDaoJdbc(connection).deleteSpend(
                            SpendEntity.fromJson(spendJson)
                    );
                },
                CFG.spendJdbcUrl(),
                Connection.TRANSACTION_READ_COMMITTED
        );
    }

    public CategoryJson createCategory(@NonNull CategoryJson category){
        return transaction(connection -> {
                    return CategoryJson.fromEntity(
                            new CategoryDaoJdbc(connection).create(
                                    CategoryEntity.fromJson(category)
                            )
                    );
                },
                CFG.spendJdbcUrl(),
                Connection.TRANSACTION_READ_COMMITTED
        );

    }

    public void deleteCategory(@NonNull CategoryJson category){
        transaction(connection -> {
                    new CategoryDaoJdbc(connection).deleteCategory(
                            CategoryEntity.fromJson(category)
                    );
                },
                CFG.spendJdbcUrl(),
                Connection.TRANSACTION_READ_COMMITTED
        );
    }
}