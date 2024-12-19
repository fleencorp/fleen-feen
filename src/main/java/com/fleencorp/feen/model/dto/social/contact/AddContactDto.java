package com.fleencorp.feen.model.dto.social.contact;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.social.ContactType;
import com.fleencorp.feen.model.domain.social.Contact;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AddContactDto {

  @NotBlank(message = "{share.contact.NotBlank}")
  @Size(min = 1, max = 1000, message = "{share.contact.Size}")
  @JsonProperty("contact")
  private String contact;

  @NotNull(message = "{share.contactType.NotNull}")
  @OneOf(enumClass = ContactType.class, message = "{share.contactType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("contact_type")
  private String contactType;

  public Contact toContact(final Member member) {
    final Contact contact = toContact();
    contact.setOwner(member);
    return contact;
  }

  public Contact toContact() {
    return Contact.builder()
        .contactValue(contact)
        .contactType(getActualContactType())
        .build();
  }

  public ContactType getActualContactType() {
    return ContactType.of(contactType);
  }
}
