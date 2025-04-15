package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthorityEntity implements Serializable {

  private UUID id;

  private UUID userId;

  private Authority authority;

  public static AuthorityEntity fromJson(AuthorityJson json){
    AuthorityEntity entity = new AuthorityEntity();
    entity.setId(json.id());
    entity.setUserId(json.userId());
    entity.setAuthority(json.authority());
    return entity;
  }

}
