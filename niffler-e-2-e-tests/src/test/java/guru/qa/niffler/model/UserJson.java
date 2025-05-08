package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        @JsonProperty("friendshipStatus")
        FriendshipStatus friendshipStatus,
        @JsonIgnore
        TestData testData
) {
    public static UserJson fromEntity(UserEntity userEntity, FriendshipStatus friendshipStatus) {
        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getSurname(),
                userEntity.getFullname(),
                userEntity.getCurrency(),
                userEntity.getPhoto() != null && userEntity.getPhoto().length > 0 ? new String(userEntity.getPhoto(), StandardCharsets.UTF_8) : null,
                userEntity.getPhotoSmall() != null && userEntity.getPhotoSmall().length > 0 ? new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
                friendshipStatus,
                new TestData(
                        null,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
    }

    public UserJson withPassword(String password) {
        return withTestData(
                new TestData(
                        password,
                        testData.categories(),
                        testData.spendings(),
                        testData.incomeInvitations(),
                        testData.outcomeInvitations(),
                        testData.friends()
                )
        );
    }

    public UserJson withTestData(TestData testData) {
        return new UserJson(
                id,
                username,
                firstname,
                surname,
                fullname,
                currency,
                photo,
                photoSmall,
                friendshipStatus,
                testData
        );
    }
}