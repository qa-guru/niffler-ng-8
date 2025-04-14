package guru.qa.niffler.db.entity.auth;

import guru.qa.niffler.api.model.Authority;
import guru.qa.niffler.api.model.AuthorityJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class AuthAuthorityEntity implements Serializable {

    private UUID id;
    private AuthUserEntity user;
    private Authority authority;

    public static AuthAuthorityEntity fromJson(AuthorityJson json) {
        return new AuthAuthorityEntity()
                .setId(json.getId())
                .setAuthority(json.getAuthority());
    }

    public static List<AuthAuthorityEntity> fromJson(List<AuthorityJson> jsons) {
        return jsons.stream()
                .map(AuthAuthorityEntity::fromJson)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "AuthAuthorityEntity{" +
                "authority=" + authority +
                ", id=" + id +
                ", userId=" + user.getId() +
                '}';
    }
}
