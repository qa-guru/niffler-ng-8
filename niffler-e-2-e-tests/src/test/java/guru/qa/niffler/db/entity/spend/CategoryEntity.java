package guru.qa.niffler.db.entity.spend;

import guru.qa.niffler.api.model.CategoryJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CategoryEntity implements Serializable {

    private UUID id;
    private String name;
    private String username;
    private boolean archived;

    public CategoryEntity() {
    }

    public CategoryEntity(UUID id, String name, String username, boolean archived) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.archived = archived;
    }

    public static CategoryEntity fromJson(CategoryJson json) {
        return new CategoryEntity(
                json.id(),
                json.name(),
                json.username(),
                json.archived()
        );
    }

}