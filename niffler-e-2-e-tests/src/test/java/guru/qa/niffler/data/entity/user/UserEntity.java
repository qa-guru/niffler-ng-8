package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.users.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {

    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String fullName;
    private String firstName;
    private String surname;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserEntity fromJson(UserJson json) {
        UserEntity se = new UserEntity();
        se.setId(json.id());
        se.setUsername(null);
        se.setCurrency(json.currency());
        se.setFullName(json.fullname());
        se.setFirstName(json.firstname());
        se.setSurname(json.surname());
        return se;
    }
}