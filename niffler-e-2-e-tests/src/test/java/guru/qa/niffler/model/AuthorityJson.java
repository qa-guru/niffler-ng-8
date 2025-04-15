package guru.qa.niffler.model;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.UUID;

public record AuthorityJson(
        UUID id,
        UUID userId,
        Authority authority
) {
    public AuthorityJson fromEntity(AuthorityEntity entity){
        return new AuthorityJson(
                entity.getId(),
                entity.getUserId(),
                entity.getAuthority()
        );
    }
}
