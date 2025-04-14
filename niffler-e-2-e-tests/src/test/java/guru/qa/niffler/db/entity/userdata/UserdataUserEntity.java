package guru.qa.niffler.db.entity.userdata;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.api.model.UserdataUserJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UserdataUserEntity implements Serializable {

    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullname;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserdataUserEntity fromJson(UserdataUserJson json) {
        return new UserdataUserEntity()
                .setId(json.getId())
                .setUsername(json.getUsername())
                .setCurrency(json.getCurrency())
                .setFirstname(json.getFirstname())
                .setSurname(json.getSurname())
                .setFullname(json.getFullname())
                .setPhoto(json.getPhoto())
                .setPhotoSmall(json.getPhotoSmall());
    }

}