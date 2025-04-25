package guru.qa.niffler.model.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.user.AuthUserEntity;

import java.util.UUID;

public record AuthUserJson(@JsonProperty("id")
                           UUID id,

                           @JsonProperty("username")
                           String username,

                           @JsonProperty("username")
                           String password,

                           @JsonProperty("enabled")
                           Boolean enabled,

                           @JsonProperty("accountNonExpired")
                           Boolean accountNonExpired,

                           @JsonProperty("accountNonLocked")
                           Boolean accountNonLocked,

                           @JsonProperty("credentialsNonExpired")
                           Boolean credentialsNonExpired) {

    public static AuthUserJson fromEntity(AuthUserEntity entity) {
        return new AuthUserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEnabled(),
                entity.getAccountNonExpired(),
                entity.getAccountNonLocked(),
                entity.getCredentialsNonExpired()
        );
    }
}