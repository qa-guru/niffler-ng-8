package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.model.CurrencyValues;
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
}