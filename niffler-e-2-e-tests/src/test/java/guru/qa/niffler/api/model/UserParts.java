package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.util.TestData;
import lombok.Data;

import java.util.UUID;

@Data
public class UserParts {

    private final AuthUserJson authUserJson;
    private final UserdataUserJson userdataUserJson;
    private TestData testData;

    public UserParts(AuthUserJson authUserJson, UserdataUserJson userdataUserJson) {
        this.authUserJson = authUserJson;
        this.userdataUserJson = userdataUserJson;
        this.testData = new TestData();
    }

    public static UserParts of(String username) {
        AuthUserJson authUserJson = new AuthUserJson().setUsername(username);
        UserdataUserJson userdataUserJson = new UserdataUserJson().setUsername(username);
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

    public void setPassword(String password) {
        authUserJson.setPassword(password);
    }

    public void setUsername(String name) {
        authUserJson.setUsername(name);
        userdataUserJson.setUsername(name);
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

}