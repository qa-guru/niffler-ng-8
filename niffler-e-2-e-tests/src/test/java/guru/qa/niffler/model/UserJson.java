package guru.qa.niffler.model;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.UUID;

public record UserJson(
        UUID id,
        String username,
        CurrencyValues currency,
        String firstname,
        String surname,
        String fullname,
        byte[] photo,
        byte[] photoSmall) {

    public static UserJson fromEntity(UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getCurrency(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),
                entity.getPhoto(),
                entity.getPhotoSmall()
        );
    }
}
