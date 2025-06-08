package com.fleencorp.feen.contact.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.contact.constant.ContactType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteContactDto {

  @Valid
  @NotNull(message = "{contact.contacts.NotEmpty}")
  @Size(max = 12, message = "{contact.contacts.Size}")
  @JsonProperty("contacts")
  private List<ContactDto> contacts = new ArrayList<>();

  public List<ContactType> getContactTypes() {
    return contacts.stream()
      .filter(Objects::nonNull)
      .map(ContactDto::getContactType)
      .filter(Objects::nonNull)
      .toList();
  }

  @Getter
  @Setter
  public static class ContactDto {

    @NotNull(message = "{contact.contactType.NotNull}")
    @OneOf(enumClass = ContactType.class, message = "{contact.contactType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty("contact_type")
    private String contactType;

    public ContactType getContactType() {
      return ContactType.of(contactType);
    }
  }
}
