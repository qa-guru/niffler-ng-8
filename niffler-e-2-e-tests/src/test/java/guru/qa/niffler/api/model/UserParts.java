package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import lombok.Data;

import java.util.UUID;

@Data
public class UserParts {

    private final AuthUserJson authUserJson;
    private final UserdataUserJson userdataUserJson;

    public UserParts(AuthUserJson authUserJson, UserdataUserJson userdataUserJson) {
        this.authUserJson = authUserJson;
        this.userdataUserJson = userdataUserJson;
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