package com.fleencorp.feen.stream.model.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.constant.mask.MaskedEmailAddress;
import com.fleencorp.feen.common.constant.mask.MaskedPhoneNumber;
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
  "phone",
  "is_organizer"
})
public class Organizer {

  @JsonProperty("name")
  private String organizerName;

  @JsonProperty("email")
  private MaskedEmailAddress organizerEmail;

  @JsonProperty("phone")
  private MaskedPhoneNumber organizerPhone;

  @JsonProperty("is_organizer")
  private Boolean isOrganizer;

  public static Organizer of(final String organizerName, final String organizerEmail, final String organizerPhone) {
    final MaskedEmailAddress organizerEmailAddress = MaskedEmailAddress.of(organizerEmail);
    final MaskedPhoneNumber organizerPhoneNumber = MaskedPhoneNumber.of(organizerPhone);

    final Organizer organizer = new Organizer();
    organizer.setOrganizerName(organizerName);
    organizer.setOrganizerEmail(organizerEmailAddress);
    organizer.setOrganizerPhone(organizerPhoneNumber);

    return organizer;
  }
}
