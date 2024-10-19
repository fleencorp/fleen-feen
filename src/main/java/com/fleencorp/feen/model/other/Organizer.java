package com.fleencorp.feen.model.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
import com.fleencorp.feen.constant.security.mask.MaskedPhoneNumber;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "name",
  "email",
  "phone"
})
public class Organizer {

  @JsonProperty("name")
  private String organizerName;

  @JsonProperty("email")
  private MaskedEmailAddress organizerEmail;

  @JsonProperty("phone")
  private MaskedPhoneNumber organizerPhone;

  public static Organizer of(final String organizerName, final String organizerEmail, final String organizerPhone) {
    return Organizer.builder()
      .organizerName(organizerName)
      .organizerEmail(MaskedEmailAddress.of(organizerEmail))
      .organizerPhone(MaskedPhoneNumber.of(organizerPhone))
      .build();
  }
}
