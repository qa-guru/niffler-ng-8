package guru.qa.niffler.model.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import javax.annotation.Nonnull;
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

    public static @Nonnull AuthUserJson fromEntity(@Nonnull AuthUserEntity entity) {
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