package com.fleencorp.feen.contact.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "can_request_contact",
  "can_request_contact_text",
  "can_request_contact_text_2",
  "can_request_contact_text_3"
})
public class ContactRequestEligibilityInfo {

  @JsonProperty("can_request_contact")
  private Boolean canRequestContact;

  @JsonProperty("can_request_contact_text")
  private String canRequestContactText;

  @JsonProperty("can_request_contact_text_2")
  private String canRequestContactText2;

  @JsonProperty("can_request_contact_text_3")
  private String canRequestContactText3;

  public static ContactRequestEligibilityInfo of(final Boolean canRequestContact, final String canRequestContactText, final String canRequestContactText2, final String canRequestContactText3) {
    return new ContactRequestEligibilityInfo(canRequestContact, canRequestContactText, canRequestContactText2, canRequestContactText3);
  }
}

