package com.fleencorp.feen.model.response.social.contact;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
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
public class UpdateContactResponse extends ApiResponse {

  @JsonProperty("contact")
  private ContactResponse contact;

  @Override
  public String getMessageCode() {
    return "update.contact";
  }

  public static UpdateContactResponse of(final ContactResponse contactResponse) {
    return new UpdateContactResponse(contactResponse);
  }
}
