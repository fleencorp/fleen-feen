package com.fleencorp.feen.contact.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.contact.model.info.ContactTypeInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "contact_types"
})
public class GetAvailableContactTypeResponse extends LocalizedResponse {

  @JsonProperty("contact_types")
  private Map<ContactType, ContactTypeInfo> contactTypes;

  @Override
  public String getMessageCode() {
    return "get.available.contact.type";
  }

  public static GetAvailableContactTypeResponse of(final Map<ContactType, ContactTypeInfo> contactTypes) {
    return new GetAvailableContactTypeResponse(contactTypes);
  }
}
