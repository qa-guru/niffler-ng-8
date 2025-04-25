package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.data.enums.AuthorityRoles;
import guru.qa.niffler.model.users.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private List<AuthorityRoles> roles = new ArrayList<>();

    public static AuthUserEntity fromJson(UserJson json) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setId(json.id());
        aue.setUsername(json.username());
        aue.setPassword(json.password());
        aue.setEnabled(true);
        aue.setAccountNonExpired(true);
        aue.setAccountNonLocked(true);
        aue.setCredentialsNonExpired(true);
        return aue;
    }
}