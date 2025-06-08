package com.fleencorp.feen.contact.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class ContactDeleteResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "contact.delete";
  }

  public static ContactDeleteResponse of() {
    return new ContactDeleteResponse();
  }
}
