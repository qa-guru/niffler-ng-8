package guru.qa.niffler.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthTokenResponse(@JsonProperty("access_token") String accessToken,
                                 @JsonProperty("refresh_token") String refreshToken,
                                 @JsonProperty("token_type") String tokenType,
                                 @JsonProperty("expires_in") Integer expiresIn, @JsonProperty("scope") String scope,
                                 @JsonProperty("id_token") String idToken) {
}
