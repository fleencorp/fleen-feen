package com.fleencorp.feen.contact.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.contact.model.response.base.ContactResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "message",
  "contact"
})
public class ContactUpdateResponse extends LocalizedResponse {

  @JsonProperty("contact")
  private ContactResponse contact;

  @Override
  public String getMessageCode() {
    return "contact.update";
  }

  public static ContactUpdateResponse of(final ContactResponse contactResponse) {
    return new ContactUpdateResponse(contactResponse);
  }

  public static ContactUpdateResponse of() {
    return new ContactUpdateResponse();
  }
}
