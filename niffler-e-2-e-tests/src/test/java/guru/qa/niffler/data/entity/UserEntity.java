package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
@Setter
public class UserEntity implements Serializable {
  private UUID id;
  private String username;
  private CurrencyValues currency;
  private String firstname;
  private String surname;
  private String fullname;
  private byte[] photo;
  private byte[] photoSmall;

  public static UserEntity fromJson(UserJson json) {
    UserEntity user = new UserEntity();
    user.setId(json.id());
    user.setUsername(json.username());
    user.setCurrency(json.currency());
    user.setFirstname(json.firstname());
    user.setSurname(json.surname());
    user.setFullname(json.fullname());
    user.setPhoto(json.photo().getBytes(UTF_8));
    user.setPhotoSmall(json.photoSmall().getBytes(UTF_8));
    return user;
  }
}