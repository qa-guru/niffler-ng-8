package guru.qa.niffler.api.model;

public record UserParts(
        AuthUserJson authUserJson,
        UserdataUserJson userdataUserJson) {

    public static UserParts of(String username) {
        AuthUserJson authUserJson = new AuthUserJson().setUsername(username);
        UserdataUserJson userdataUserJson = new UserdataUserJson().setUsername(username);
        return new UserParts(authUserJson, userdataUserJson);
    }

}