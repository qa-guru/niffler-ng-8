package guru.qa.niffler.service;

import guru.qa.niffler.model.users.UserJson;

import java.util.UUID;

public class UserApiClient implements UsersClient{
    @Override
    public UserJson createUserTxJdbc(UserJson user) {
        return null;
    }

    @Override
    public UserJson createUserTxChainedJdbc(UserJson user) {
        return null;
    }

    @Override
    public UserJson createUserJdbc(UserJson user) {
        return null;
    }

    @Override
    public void addIncomeInvitation(UUID requesterUUID, UUID addresseeUUID) {

    }

    @Override
    public void addIncomeInvitation(UserJson requester, UserJson addressee) {

    }

    @Override
    public void addOutcomeInvitation(UUID requesterUUID, UUID addresseeUUID) {

    }

    @Override
    public void addFriend(UUID requesterUUID, UUID addresseeUUID) {

    }

    @Override
    public void deleteUser(UserJson user) {

    }

    @Override
    public UUID getUserId(UserJson user) {
        return null;
    }

    @Override
    public void clearPhotoDataByUsername(String username) {

    }
}
