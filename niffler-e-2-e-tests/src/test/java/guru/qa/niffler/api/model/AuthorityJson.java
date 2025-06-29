package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class AuthorityJson {

    private UUID id;
    private UUID userId;
    private Authority authority;

    public static @Nonnull AuthorityJson fromEntity(@Nonnull AuthAuthorityEntity entity) {
        return new AuthorityJson()
                .setId(entity.getId())
                .setAuthority(entity.getAuthority())
                .setUserId(entity.getUser().getId());
    }

    public static @Nonnull List<AuthorityJson> fromEntity(@Nonnull List<AuthAuthorityEntity> entities) {
        return entities.stream()
                .map(AuthorityJson::fromEntity)
                .collect(Collectors.toList());
    }

}
