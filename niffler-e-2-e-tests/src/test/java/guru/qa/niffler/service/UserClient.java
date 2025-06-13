package guru.qa.niffler.service;

import guru.qa.niffler.api.model.UserParts;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserClient {

    default Optional<UserParts> findByAuthId(String id) {
        return findByAuthId(UUID.fromString(id));
    }

    Optional<UserParts> findByAuthId(UUID id);

    Optional<UserParts> findByUsername(String username);

    UserParts createUser(UserParts userPart);

    UserParts updateUser(UserParts userPart);

    List<UserParts> findAll();

    void deleteUser(UserParts userPart);

    void createIncomeInvitation(UserParts targetUser, int count);

    void createOutcomeInvitation(UserParts targetUser, int count);

    void createFriends(UserParts targetUser, int count);

    void deleteAllGenUser();

}
