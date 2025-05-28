package guru.qa.niffler.model.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.enums.CurrencyValues;


import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("photoSmall")
        String photoSmall,

        String password) {

    public static @Nonnull UserJson fromAuthorityEntity(@Nonnull AuthAuthorityEntity authority) {
        return new UserJson(
                authority.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static @Nonnull UserJson fromEntity(@Nonnull UserEntity userEntity) {
        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstName(),
                userEntity.getSurname(),
                userEntity.getFullName(),
                userEntity.getCurrency(),
                userEntity.getPhoto() != null && userEntity.getPhoto().length > 0 ? new String(userEntity.getPhoto(), StandardCharsets.UTF_8) : null,
                userEntity.getPhotoSmall() != null && userEntity.getPhotoSmall().length > 0 ? new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
                null
        );
    }
}