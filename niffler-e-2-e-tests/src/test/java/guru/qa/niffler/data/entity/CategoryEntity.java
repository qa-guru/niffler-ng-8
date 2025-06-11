package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CategoryJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class CategoryEntity implements Serializable {
  private UUID id;
  private String name;
  private String username;
  private boolean archived;

  public static CategoryEntity fromJson(CategoryJson json) {
    CategoryEntity category = new CategoryEntity();
    category.setId(json.id());
    category.setName(json.name());
    category.setUsername(json.username());
    category.setArchived(json.archived());
    return category;
  }
}
