package com.fleencorp.feen.contact.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.contact.constant.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateContactSingleDto extends AddContactDto {

  @NotBlank(message = "{share.contact.NotBlank}")
  @Size(min = 1, max = 1000, message = "{share.contact.Size}")
  @JsonProperty("contact")
  private String contact;

  @NotNull(message = "{share.contactType.NotNull}")
  @OneOf(enumClass = ContactType.class, message = "{share.contactType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("contact_type")
  private String contactType;

  public ContactType getContactType() {
    return ContactType.of(contactType);
  }


}
