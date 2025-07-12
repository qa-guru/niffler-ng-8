package guru.qa.niffler.service.impl.db;

import guru.qa.niffler.api.model.AuthUserJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.api.model.UserdataUserJson;
import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.repository.AuthUserRepository;
import guru.qa.niffler.db.repository.UserdataUserRepository;
import guru.qa.niffler.db.repository.impl.spring_jdbc.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.db.repository.impl.spring_jdbc.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.db.tpl.XaTransactionTemplate;
import guru.qa.niffler.service.UserClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static guru.qa.niffler.util.RandomDataUtils.USERNAME_PREFIX;
import static guru.qa.niffler.util.RandomDataUtils.genDefaultUser;

@SuppressWarnings("DataFlowIssue")
@ParametersAreNonnullByDefault
public class UserDbClient extends AbstractDbClient implements UserClient {

    private final AuthUserRepository authUserRepository;
    private final UserdataUserRepository userdataUserRepository;

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());


    public UserDbClient() {
        this.authUserRepository = new AuthUserRepositorySpringJdbc();
        this.userdataUserRepository = new UserdataUserRepositorySpringJdbc();
    }

    public UserDbClient(AuthUserRepository authUserRepository, UserdataUserRepository userdataUserRepository) {
        this.authUserRepository = authUserRepository;
        this.userdataUserRepository = userdataUserRepository;
    }

    @Step("Поиск пользователя по id")
    public @Nonnull Optional<UserParts> findByAuthId(String id) {
        return findByAuthId(UUID.fromString(id));
    }

    @Step("Поиск пользователя по id")
    public @Nonnull Optional<UserParts> findByAuthId(UUID id) {
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

    @Step("Поиск пользователя по имени")
    public @Nonnull Optional<UserParts> findByUsername(String username) {
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

    @Step("Создание пользователя")
    public @Nonnull UserParts createUser(UserParts userPart) {
        return xaTxTemplate.execute(() -> createUseWithoutTx(userPart));
    }

    private @Nonnull UserParts createUseWithoutTx(UserParts userJson) {
        AuthUserEntity authUser = authUserRepository.create(userJson.getAuthUserEntity());
        UserdataUserEntity userdataUser = userdataUserRepository.create(userJson.getUserdataUserEntity());
        return UserParts.of(authUser, userdataUser);
    }

    @Step("Обновление пользователя")
    public @Nonnull UserParts updateUser(UserParts userPart) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserRepository.update(userPart.getAuthUserEntity());
            UserdataUserEntity userdataUser = userdataUserRepository.update(userPart.getUserdataUserEntity());
            return UserParts.of(authUser, userdataUser);
        });
    }

    @Step("Получение всех пользователей")
    public @Nonnull List<UserParts> findAll() {
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

    @Step("Удаление пользователя")
    public void deleteUser(UserParts userPart) {
        xaTxTemplate.execute(() -> {
            deleteAuthUserAndAuthority(userPart.getAuthUserJson());
            deleteUserdataUser(userPart.getUserdataUserJson());
        });
    }

    public List<UserdataFriendshipEntity> selectFriendshipsByRequesterId(@Nullable UUID requesterId) {
        return selectFriendships(requesterId, null, null);
    }

    public List<UserdataFriendshipEntity> selectFriendshipsByAddresseeId(@Nullable UUID addresseeId) {
        return selectFriendships(null, addresseeId, null);
    }

    @Step("Получение дружеских отношений пользователя")
    public List<UserdataFriendshipEntity> selectFriendships(@Nullable UUID requesterId,
                                                            @Nullable UUID addresseeId,
                                                            @Nullable FriendshipStatus status) {
        return userdataUserRepository.selectFriendships(requesterId, addresseeId, status);
    }

    @Step("Создание входящих запросов на дружбу")
    @Override
    public void createIncomeInvitation(UserParts targetUser, int count) {
        xaTxTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserParts userJson = genDefaultUser();
                UserParts createdUser = createUseWithoutTx(userJson);
                userdataUserRepository.addIncomeInvitation(
                    targetUser.getUserdataUserEntity(), createdUser.getUserdataUserEntity()
                );
                targetUser.getTestData().getInInviteNames().add(createdUser.getUsername());
            }
        });
    }

    @Step("Создание исходящих запросов на дружбу")
    @Override
    public void createOutcomeInvitation(UserParts targetUser, int count) {
        xaTxTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserParts userJson = genDefaultUser();
                UserParts createdUser = createUseWithoutTx(userJson);
                userdataUserRepository.addOutcomeInvitation(
                    targetUser.getUserdataUserEntity(), createdUser.getUserdataUserEntity()
                );
                targetUser.getTestData().getOutInviteNames().add(createdUser.getUsername());
            }
        });
    }

    @Step("Создание дружеских связей для пользователя")
    @Override
    public void createFriends(UserParts targetUser, int count) {
        xaTxTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserParts userJson = genDefaultUser();
                UserParts createdUser = createUseWithoutTx(userJson);
                userdataUserRepository.addFriend(
                    targetUser.getUserdataUserEntity(), createdUser.getUserdataUserEntity()
                );
                targetUser.getTestData().getFriendsNames().add(createdUser.getUsername());
            }
        });
    }

    @Step("Удаление сгенерированных пользователей")
    @Override
    public void deleteAllGenUser() {
        findAll().stream()
            .filter(u -> u.getUsername().startsWith(USERNAME_PREFIX))
            .forEach(this::deleteUser);
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
