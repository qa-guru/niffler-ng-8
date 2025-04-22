package guru.qa.niffler.data.entity.auth;


import guru.qa.niffler.model.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
public class AuthorityEntity implements Serializable {
    private UUID id;
    private UUID userId;
    private Authority authority;

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity authAuthority = new AuthorityEntity();
        authAuthority.setId(json.id());
        authAuthority.setUserId(json.userId());
        authAuthority.setAuthority(json.authority());
        return authAuthority;
    }
}