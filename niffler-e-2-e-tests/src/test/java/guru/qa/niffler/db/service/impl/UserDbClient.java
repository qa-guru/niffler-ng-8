package guru.qa.niffler.db.service.impl;

import guru.qa.niffler.api.model.AuthUserJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.repository.AuthUserRepository;
import guru.qa.niffler.db.repository.UserdataUserRepository;
import guru.qa.niffler.db.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.db.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.db.service.UserClient;
import guru.qa.niffler.db.tpl.XaTransactionTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;

public class UserDbClient extends AbstractDbClient implements UserClient {

    private final AuthUserRepository authUserRepository;
    private final UserdataUserRepository userdataUserRepository;

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());


    public UserDbClient() {
        this.authUserRepository = new AuthUserRepositoryHibernate();
        this.userdataUserRepository = new UserdataUserRepositoryHibernate();
    }

    public UserDbClient(AuthUserRepository authUserRepository, UserdataUserRepository userdataUserRepository) {
        this.authUserRepository = authUserRepository;
        this.userdataUserRepository = userdataUserRepository;
    }

    public Optional<UserParts> findByAuthId(String id) {
        return findByAuthId(UUID.fromString(id));
    }

    public Optional<UserParts> findByAuthId(UUID id) {
        Optional<AuthUserEntity> authUserOpt = authUserRepository.findById(id);
        if (authUserOpt.isPresent()) {
            AuthUserEntity authUser = authUserOpt.get();
            Optional<UserdataUserEntity> userdataUserOpt = userdataUserRepository.findByUsername(authUser.getUsername());
            if (userdataUserOpt.isPresent()) {
                return Optional.of(UserParts.of(authUser, userdataUserOpt.get()));
            } else {
                throw new IllegalStateException("AuthUser найден, а UserdataUser нет");
            }
        }
        return Optional.empty();
    }

    public Optional<UserParts> findByUsername(String username) {
        Optional<AuthUserEntity> authUserOpt = authUserRepository.findByUsername(username);
        if (authUserOpt.isPresent()) {
            AuthUserEntity authUser = authUserOpt.get();
            Optional<UserdataUserEntity> userdataUserOpt = userdataUserRepository.findByUsername(username);
            if (userdataUserOpt.isPresent()) {
                return Optional.of(UserParts.of(authUser, userdataUserOpt.get()));
            } else {
                throw new IllegalStateException("AuthUser найден, а UserdataUser нет");
            }
        }
        return Optional.empty();
    }

    public UserParts createUser(UserParts userJson) {
        return xaTxTemplate.execute(() -> createUseWithoutTx(userJson));
    }

    private UserParts createUseWithoutTx(UserParts userJson) {
        AuthUserEntity authUser = authUserRepository.create(userJson.getAuthUserEntity());
        UserdataUserEntity userdataUser = userdataUserRepository.create(userJson.getUserdataUserEntity());
        return UserParts.of(authUser, userdataUser);
    }

    public UserParts updateUser(UserParts userJson) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserRepository.update(userJson.getAuthUserEntity());
            UserdataUserEntity userdataUser = userdataUserRepository.update(userJson.getUserdataUserEntity());
            return UserParts.of(authUser, userdataUser);
        });
    }

    public List<UserParts> findAll() {
        return xaTxTemplate.execute(() -> {
            List<UserParts> result = new ArrayList<>();
            Map<String, UserdataUserEntity> userdataUserByName = userdataUserRepository.findAll().stream()
                    .collect(Collectors.toMap(UserdataUserEntity::getUsername, Function.identity()));
            for (AuthUserEntity authUser : authUserRepository.findAll()) {
                UserdataUserEntity userdataUser = userdataUserByName.get(authUser.getUsername());
                result.add(UserParts.of(authUser, userdataUser));
            }
            return result;
        });
    }

    public void deleteUser(UserParts userJson) {
        xaTxTemplate.execute(() -> {
            deleteAuthUserAndAuthority(userJson.getAuthUserJson());
            deleteUserdataUser(userJson.getUserdataUserJson());
        });
    }

    @Override
    public void createIncomeInvitation(UserParts targetUser, int count) {
        xaTxTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserParts userJson = genDefaultUser();
                UserParts createdUser = createUseWithoutTx(userJson);
                userdataUserRepository.addOutcomeInvitation(
                    targetUser.getUserdataUserEntity(), createdUser.getUserdataUserEntity()
                );
            }
        });
    }

    @Override
    public void createOutcomeInvitation(UserParts targetUser, int count) {
        xaTxTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserParts userJson = genDefaultUser();
                UserParts createdUser = createUseWithoutTx(userJson);
                userdataUserRepository.addIncomeInvitation(
                    targetUser.getUserdataUserEntity(), createdUser.getUserdataUserEntity()
                );
            }
        });
    }

    @Override
    public void createFriends(UserParts targetUser, int count) {
        xaTxTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserParts userJson = genDefaultUser();
                UserParts createdUser = createUseWithoutTx(userJson);
                userdataUserRepository.addFriend(
                    targetUser.getUserdataUserEntity(), createdUser.getUserdataUserEntity()
                );
            }
        });
    }

    private void deleteUserdataUser(UserdataUserJson userJson) {
        UserdataUserEntity userdataUser;
        if (userJson.getId() != null) {
            userdataUser = UserdataUserEntity.fromJson(userJson);
        } else if (userJson.getUsername() != null) {
            String username = userJson.getUsername();
            userdataUser = userdataUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username: " +
                                    username
                    ));
        } else {
            throw new IllegalArgumentException("id и username == null");
        }
        userdataUserRepository.delete(userdataUser);
    }

    private void deleteAuthUserAndAuthority(AuthUserJson userJson) {
        AuthUserEntity authUser;
        if (userJson.getId() != null) {
            authUser = AuthUserEntity.fromJson(userJson);
        } else if (userJson.getUsername() != null) {
            String username = userJson.getUsername();
            authUser = authUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "У пользователя невозможно удалить, т.к. отсутствует id и не получается найти по username: " +
                                    username
                    ));
        } else {
            throw new IllegalArgumentException("id и username == null");
        }
        authUserRepository.delete(authUser);
    }

}
