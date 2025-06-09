package com.fleencorp.feen.contact.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.contact.model.domain.Contact;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContactDto {

  @Valid
  @NotEmpty(message = "{contact.contacts.NotEmpty}")
  @Size(max = 12, message = "{contact.contacts.Size}")
  @JsonProperty("contacts")
  private Collection<ContactDto> contacts = new ArrayList<>();

  public List<ContactDto> getContacts() {
    if (contacts == null || contacts.isEmpty()) {
      return Collections.emptyList();
    }

    final Map<ContactType, ContactDto> uniqueByType = new LinkedHashMap<>();
    for (final ContactDto dto : contacts) {
      if (nonNull(dto) && dto.isValid()) {
        uniqueByType.putIfAbsent(dto.getContactType(), dto);
      }
    }

    return new ArrayList<>(uniqueByType.values());
  }

  public Set<ContactType> getValidContactTypes() {
    return contacts.stream()
      .filter(ContactDto::isValid)
      .map(ContactDto::getContactType)
      .collect(Collectors.toSet());
  }

  @Getter
  @Setter
  public static class ContactDto {

    @NotBlank(message = "{contact.contactValue.NotBlank}")
    @Size(min = 1, max = 1000, message = "{contact.contactValue.Size}")
    @JsonProperty("contact")
    private String contact;

    @NotNull(message = "{contact.contactType.NotNull}")
    @OneOf(enumClass = ContactType.class, message = "{contact.contactType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty("contact_type")
    private String contactType;

    public ContactType getContactType() {
      return ContactType.of(contactType);
    }

    public boolean isValid() {
      return contact != null && !contact.isBlank() && getContactType() != null;
    }

    public boolean isInvalid() {
      return !isValid();
    }

    public Contact toContact(final Member member) {
      final Contact contact = new Contact();
      contact.setOwner(member);
      contact.setContactType(getContactType());
      contact.setContactValue(this.contact);
      return contact;
    }
  }
}

