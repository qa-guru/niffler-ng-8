package guru.qa.niffler.db.entity.auth;

import guru.qa.niffler.api.model.UserJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class AuthUserEntity implements Serializable {

    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private List<AuthAuthorityEntity> authorities = new ArrayList<>();

    public void addAuthorities(AuthAuthorityEntity... authorities) {
        addAuthorities(Arrays.asList(authorities));
    }

    public void addAuthorities(List<AuthAuthorityEntity> authorities) {
        authorities.forEach(au -> au.setUser(this));
        this.authorities.addAll(authorities);
    }

    public void addAuthorities(AuthAuthorityEntity authority) {
        this.authorities.add(authority);
        authority.setUser(this);
    }

    public void removeAuthority(AuthAuthorityEntity authority) {
        this.authorities.remove(authority);
    }

    public static AuthUserEntity fromJson(UserJson json) {
        return new AuthUserEntity()
                .setId(json.getAuthId())
                .setUsername(json.getUsername())
                .setPassword(json.getPassword())
                .setEnabled(json.getEnabled())
                .setAccountNonExpired(json.getAccountNonExpired())
                .setAccountNonLocked(json.getAccountNonLocked())
                .setCredentialsNonExpired(json.getCredentialsNonExpired())
                .setAuthorities(AuthAuthorityEntity.fromJson(json.getAuthorities()));
    }

}
