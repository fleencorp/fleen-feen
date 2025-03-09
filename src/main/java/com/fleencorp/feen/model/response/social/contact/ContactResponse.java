package com.fleencorp.feen.model.response.social.contact;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.social.ContactType;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "contact",
  "contact_type",
  "contact_type_label",
  "created_on",
  "updated_on"
})
public class ContactResponse extends FleenFeenResponse {

  @JsonProperty("contact")
  private String contact;

  @JsonFormat(shape = STRING)
  @JsonProperty("contact_type")
  private ContactType contactType;

  @JsonProperty("contact_type_label")
  private String contactTypeLabel;
}
