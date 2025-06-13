package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.util.TestData;
import lombok.Data;

import java.util.UUID;

@Data
public class UserParts {

    private AuthUserJson authUserJson;
    private UserdataUserJson userdataUserJson;
    private TestData testData;

    public UserParts(AuthUserJson authUserJson, UserdataUserJson userdataUserJson) {
        this.authUserJson = authUserJson;
        this.userdataUserJson = userdataUserJson;
        this.testData = new TestData();
    }

    public static UserParts of(String username, String password) {
        UserParts userParts = UserParts.of(username);
        userParts.setPassword(password);
        return userParts;
    }

    public static UserParts of(String username) {
        AuthUserJson authUserJson = new AuthUserJson().setUsername(username);
        UserdataUserJson userdataUserJson = new UserdataUserJson().setUsername(username);
        return new UserParts(authUserJson, userdataUserJson);
    }

    public static UserParts of(UserdataUserJson userdataUserJson, String username, String password) {
        AuthUserJson authUserJson = new AuthUserJson()
            .setUsername(username)
            .setPassword(password);
        return new UserParts(authUserJson, userdataUserJson);
    }

    public static UserParts of(UserdataUserJson userdataUserJson) {
        AuthUserJson authUserJson = new AuthUserJson()
            .setUsername(userdataUserJson.getUsername());
        return new UserParts(authUserJson, userdataUserJson);
    }

    public static UserParts of(AuthUserEntity authUserEntity, UserdataUserEntity userdataUserEntity) {
        AuthUserJson authUserJson = AuthUserJson.fromEntity(authUserEntity);
        UserdataUserJson userdataUserJson = UserdataUserJson.fromEntity(userdataUserEntity);
        return new UserParts(authUserJson, userdataUserJson);
    }

    public AuthUserEntity getAuthUserEntity() {
        return AuthUserEntity.fromJson(authUserJson);
    }

    public UserdataUserEntity getUserdataUserEntity() {
        return UserdataUserEntity.fromJson(userdataUserJson);
    }

    public String getUsername() {
        return authUserJson.getUsername();
    }

    public String getPassword() {
        return authUserJson.getPassword();
    }

    public String getUserdataId() {
        return userdataUserJson.getUsername();
    }

    public UUID getAuthId() {
        return authUserJson.getId();
    }

    public AuthUserJson getAuthUserJson() {
        return authUserJson;
    }

    public UserdataUserJson getUserdataUserJson() {
        return userdataUserJson;
    }

    public UserParts setPassword(String password) {
        authUserJson.setPassword(password);
        return this;
    }

    public UserParts setUsername(String name) {
        authUserJson.setUsername(name);
        userdataUserJson.setUsername(name);
        return this;
    }

    public UserParts setUserdataUser(UserdataUserJson userdataUserJson) {
        this.userdataUserJson = userdataUserJson;
        return this;
    }

}