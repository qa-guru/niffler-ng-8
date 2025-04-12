package guru.qa.niffler.api.model;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UserJson {

    //authDB
    private UUID authId;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private List<AuthorityJson> authorities = new ArrayList<>();
    //userdataDB
    private UUID userdataId;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullname;
    private byte[] photo;
    private byte[] photoSmall;

    public void addAuthorities(AuthorityJson... authorities) {
        List<AuthorityJson> authorityList = Arrays.stream(authorities)
                .peek(au -> au.setUserId(this.getAuthId()))
                .toList();
        this.authorities.addAll(authorityList);
    }

    public void addAuthorities(AuthorityJson authority) {
        this.authorities.add(authority);
        authority.setUserId(this.getAuthId());
    }

    public void removeAuthority(AuthorityJson authority) {
        this.authorities.remove(authority);
    }

    public static UserJson fromEntity(AuthUserEntity authEntity, UserdataUserEntity userdataEntity) {
        return new UserJson()
                .setAuthId(authEntity.getId())
                .setUsername(authEntity.getUsername())
                .setPassword("encrypted")
                .setEnabled(authEntity.getEnabled())
                .setAccountNonExpired(authEntity.getAccountNonExpired())
                .setAccountNonLocked(authEntity.getAccountNonLocked())
                .setCredentialsNonExpired(authEntity.getCredentialsNonExpired())
                .setAuthorities(AuthorityJson.fromEntity(authEntity.getAuthorities()))

                .setUserdataId(userdataEntity.getId())
                .setCurrency(userdataEntity.getCurrency())
                .setFirstname(userdataEntity.getFirstname())
                .setSurname(userdataEntity.getSurname())
                .setFullname(userdataEntity.getFullname())
                .setPhoto(userdataEntity.getPhoto())
                .setPhotoSmall(userdataEntity.getPhotoSmall());
    }

}
