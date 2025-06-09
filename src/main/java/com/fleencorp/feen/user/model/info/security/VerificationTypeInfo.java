package com.fleencorp.feen.user.model.info.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.user.constant.verification.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "verification_type",
  "verification_type_text"
})
public class VerificationTypeInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("verification_type")
  private VerificationType verificationType;

  @JsonProperty("verification_type_text")
  private String verificationTypeText;

  public static VerificationTypeInfo of(final VerificationType verificationType, final String verificationTypeText) {
    return new VerificationTypeInfo(verificationType, verificationTypeText);
  }
}
