package com.fleencorp.feen.model.info.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
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
    return VerificationTypeInfo.builder()
      .verificationType(verificationType)
      .verificationTypeText(verificationTypeText)
      .build();
  }
}
