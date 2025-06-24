package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public record CategoryJson(
    UUID id,
    String name,
    String username,
    boolean archived) {

    public static CategoryJson of(String name) {
        return new CategoryJson(null, name, null, false);
    }

    public static @Nonnull CategoryJson fromEntity(@Nonnull CategoryEntity entity) {
        return new CategoryJson(
                entity.getId(),
                entity.getName(),
                entity.getUsername(),
                entity.isArchived()
        );
    }
}