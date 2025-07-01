package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.category.CategoryEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public record CategoryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("username")
        String username,
        @JsonProperty("archived")
        boolean archived) {

    public static @Nonnull CategoryJson fromEntity(@Nonnull CategoryEntity entity) {
        return
                new CategoryJson(
                        entity.getId(),
                        entity.getName(),
                        entity.getUsername(),
                        entity.isArchived()
                );
    }

    public static @Nullable CategoryJson fromEntity(Optional<CategoryEntity> optionalEntity) {
        return optionalEntity.map(entity -> new CategoryJson(
                entity.getId(),
                entity.getName(),
                entity.getUsername(),
                entity.isArchived()
        )).orElse(null);
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public boolean archived() {
        return archived;
    }
}
