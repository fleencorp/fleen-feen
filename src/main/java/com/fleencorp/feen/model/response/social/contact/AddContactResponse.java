package com.fleencorp.feen.model.response.social.contact;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "contact"
})
public class AddContactResponse extends ApiResponse {

  @JsonProperty("contact")
  private ContactResponse contact;

  @Override
  public String getMessageCode() {
    return "add.contact";
  }

  public static AddContactResponse of(final ContactResponse contactResponse) {
    return AddContactResponse.builder()
      .contact(contactResponse)
      .build();
  }
}
