package com.fleencorp.feen.model.response.share.contact;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.constant.share.ContactType;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse extends FleenFeenResponse {

  @JsonProperty("contact")
  private String contact;

  @JsonFormat(shape = STRING)
  @JsonProperty("contact_type")
  private ContactType contactType;
}
