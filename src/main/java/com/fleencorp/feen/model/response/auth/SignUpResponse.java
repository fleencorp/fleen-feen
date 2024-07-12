package com.fleencorp.feen.model.response.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationType;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "access_token",
  "refresh_token",
  "email_address",
  "phone_number",
  "authentication_status",
  "profile_verification_type"
})
public class SignUpResponse {

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
  @JsonProperty("profile_verification_type")
  private ProfileVerificationType profileVerificationType;

  @Builder.Default
  @JsonProperty("message")
  private String message = "Sign up successful";

  public static SignUpResponse of(String accessToken, String refreshToken, String emailAddress, String phoneNumber,
      AuthenticationStatus authenticationStatus, ProfileVerificationType profileVerificationType) {
    return SignUpResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .authenticationStatus(authenticationStatus)
        .profileVerificationType(profileVerificationType)
        .build();
  }

  public static SignUpResponse of(String accessToken, String refreshToken) {
    return of(accessToken, refreshToken, AuthenticationStatus.COMPLETED);
  }

  public static SignUpResponse of(String accessToken, String refreshToken, AuthenticationStatus authenticationStatus) {
    return SignUpResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .authenticationStatus(authenticationStatus)
        .profileVerificationType(null)
        .emailAddress(null)
        .phoneNumber(null)
        .build();
  }
}
