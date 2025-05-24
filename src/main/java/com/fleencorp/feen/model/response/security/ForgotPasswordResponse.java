package com.fleencorp.feen.model.response.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "email_address",
  "phone_number"
})
public class ForgotPasswordResponse extends LocalizedResponse {

  @JsonFormat(shape = STRING)
  @JsonProperty("email_address")
  private MaskedEmailAddress emailAddress;

  @JsonFormat(shape = STRING)
  @JsonProperty("phone_number")
  private MaskedPhoneNumber phoneNumber;

  @Override
  public String getMessageCode() {
    return "forgot.password";
  }

  public static ForgotPasswordResponse of(final String emailAddress, final String phoneNumber) {
    return new ForgotPasswordResponse(MaskedEmailAddress.of(emailAddress), MaskedPhoneNumber.of(phoneNumber));
  }
}
