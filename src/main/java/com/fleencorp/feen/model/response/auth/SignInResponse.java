package com.fleencorp.feen.model.response.auth;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.auth.AuthenticationStage;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "access_token",
  "refresh_token",
  "email_address",
  "phone_number",
  "authentication_status",
  "authentication_stage",
  "mfa_type",
  "mfa_enabled"
})
public class SignInResponse {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("email_address")
  private String emailAddress;

  @JsonProperty("phone_number")
  private String phoneNumber;

  @JsonFormat(shape = STRING)
  @JsonProperty("authentication_status")
  private AuthenticationStatus authenticationStatus;

  @JsonFormat(shape = STRING)
  @JsonProperty("authentication_stage")
  private AuthenticationStage authenticationStage;

  @JsonFormat(shape = STRING)
  @JsonProperty("mfa_type")
  private MfaType mfaType;

  @JsonProperty("mfa_enabled")
  private Boolean mfaEnabled;

  @Builder.Default
  @JsonProperty("message")
  private String message = "Sign-in successful";

  public static SignInResponse of(final String accessToken, final String refreshToken) {
    return of(accessToken, refreshToken, AuthenticationStatus.COMPLETED);
  }

  public static SignInResponse of(final String accessToken, final String refreshToken, final AuthenticationStatus authenticationStatus) {
    return SignInResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .authenticationStatus(authenticationStatus)
        .build();
  }

  public static SignInResponse createDefault(final String emailAddress) {
    return SignInResponse.builder()
        .emailAddress(emailAddress)
        .authenticationStatus(AuthenticationStatus.IN_PROGRESS)
        .authenticationStage(AuthenticationStage.NONE)
        .mfaEnabled(false)
        .build();
  }
}
