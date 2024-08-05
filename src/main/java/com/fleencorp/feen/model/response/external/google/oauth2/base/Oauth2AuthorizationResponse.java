package com.fleencorp.feen.model.response.external.google.oauth2.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.message.ResponseMessage.SUCCESS;


@SuperBuilder
@Getter
@Setter
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
public class Oauth2AuthorizationResponse {

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

  @Default
  private String message = SUCCESS;
}
