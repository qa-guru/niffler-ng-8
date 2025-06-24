package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.util.TestData;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Data
@ParametersAreNonnullByDefault
public class UserParts {

    private AuthUserJson authUserJson;
    private UserdataUserJson userdataUserJson;
    private TestData testData;

    public UserParts(AuthUserJson authUserJson, UserdataUserJson userdataUserJson) {
        this.authUserJson = authUserJson;
        this.userdataUserJson = userdataUserJson;
        this.testData = new TestData();
    }

    public static @Nonnull UserParts of(String username, String password) {
        UserParts userParts = UserParts.of(username);
        userParts.setPassword(password);
        return userParts;
    }

    public static @Nonnull UserParts of(String username) {
        AuthUserJson authUserJson = new AuthUserJson().setUsername(username);
        UserdataUserJson userdataUserJson = new UserdataUserJson().setUsername(username);
        return new UserParts(authUserJson, userdataUserJson);
    }

    public static @Nonnull UserParts of(UserdataUserJson userdataUserJson, String username, String password) {
        AuthUserJson authUserJson = new AuthUserJson()
            .setUsername(username)
            .setPassword(password);
        return new UserParts(authUserJson, userdataUserJson);
    }

    public static @Nonnull UserParts of(UserdataUserJson userdataUserJson) {
        AuthUserJson authUserJson = new AuthUserJson()
            .setUsername(userdataUserJson.getUsername());
        return new UserParts(authUserJson, userdataUserJson);
    }

    public static @Nonnull UserParts of(AuthUserEntity authUserEntity,
                                        UserdataUserEntity userdataUserEntity) {
        AuthUserJson authUserJson = AuthUserJson.fromEntity(authUserEntity);
        UserdataUserJson userdataUserJson = UserdataUserJson.fromEntity(userdataUserEntity);
        return new UserParts(authUserJson, userdataUserJson);
    }

    public @Nonnull AuthUserEntity getAuthUserEntity() {
        return AuthUserEntity.fromJson(authUserJson);
    }

    public @Nonnull UserdataUserEntity getUserdataUserEntity() {
        return UserdataUserEntity.fromJson(userdataUserJson);
    }

    public @Nullable String getUsername() {
        return authUserJson.getUsername();
    }

    public @Nullable String getPassword() {
        return authUserJson.getPassword();
    }

    public @Nullable String getUserdataId() {
        return userdataUserJson.getUsername();
    }

    public @Nullable UUID getAuthId() {
        return authUserJson.getId();
    }

    public @Nonnull AuthUserJson getAuthUserJson() {
        return authUserJson;
    }

    public @Nonnull UserdataUserJson getUserdataUserJson() {
        return userdataUserJson;
    }

    public @Nonnull UserParts setPassword(String password) {
        authUserJson.setPassword(password);
        return this;
    }

    public @Nonnull UserParts setUsername(String name) {
        authUserJson.setUsername(name);
        userdataUserJson.setUsername(name);
        return this;
    }

    public @Nonnull UserParts setUserdataUser(UserdataUserJson userdataUserJson) {
        this.userdataUserJson = userdataUserJson;
        return this;
    }

}