package com.fleencorp.feen.oauth2.model.response.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "id_token",
  "oauth2_client_id",
  "access_token",
  "refresh_token",
  "token_type",
  "access_token_expiration_time_in_seconds"
})
public class Oauth2AuthorizationResponse extends LocalizedResponse {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("id_token")
  private String idToken;

  @JsonProperty("oauth2_client_id")
  private String oauthClientId;

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("access_token_expiration_time_in_seconds")
  private Long accessTokenExpirationTimeInSeconds;

  @JsonProperty("scope")
  private String scope;

  @JsonIgnore
  public long getAccessTokenExpirationTimeInMilliseconds() {
    final long currentTimeMillis = System.currentTimeMillis();
    return currentTimeMillis + (accessTokenExpirationTimeInSeconds * 1000);
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "oauth2.authorization";
  }
}
