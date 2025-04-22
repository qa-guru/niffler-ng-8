package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    public static AuthUserEntity fromJson(AuthUserJson json) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setId(json.id());
        authUser.setUsername(json.username());
        authUser.setPassword(json.password());
        authUser.setEnabled(json.enabled());
        authUser.setAccountNonExpired(json.accountNonExpired());
        authUser.setAccountNonLocked(json.accountNonLocked());
        authUser.setCredentialsNonExpired(json.credentialsNonExpired());
        return authUser;
    }

}
