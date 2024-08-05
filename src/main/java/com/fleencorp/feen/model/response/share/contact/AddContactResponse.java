package com.fleencorp.feen.model.response.share.contact;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.*;
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
public class AddContactResponse extends FleenFeenResponse {

  @Builder.Default
  @JsonProperty("message")
  private String message = "Contact added successfully";

  @JsonProperty("contact")
  private ContactResponse contact;

  public static AddContactResponse of(final Long contactId, final ContactResponse contactResponse) {
    return AddContactResponse.builder()
      .id(contactId)
      .contact(contactResponse)
      .build();
  }
}
