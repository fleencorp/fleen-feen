package com.fleencorp.feen.model.response.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "email_address",
  "phone_number"
})
public class ForgotPasswordResponse {

  @JsonProperty("email_address")
  private String emailAddress;

  @JsonProperty("phone_number")
  private String phoneNumber;

  @Builder.Default
  @JsonProperty("message")
  private String message = "Forgot Password code sent successfully";

  public static ForgotPasswordResponse of(String emailAddress, String phoneNumber) {
    return ForgotPasswordResponse.builder()
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .build();
  }
}
