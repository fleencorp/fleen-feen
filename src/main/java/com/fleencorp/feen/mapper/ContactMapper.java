package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.share.Contact;
import com.fleencorp.feen.model.response.share.contact.ContactResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Utility class for mapping {@link Contact} entities to {@link ContactResponse} DTOs.
 *
 * <p>This class contains static methods to convert between {@code Contact} entities and {@code ContactResponse}
 * Data Transfer Objects (DTOs). It provides functionality to map single entities or lists of entities.</p>
 *
 * @author Yusuf ALamu Musa
 * @version 1.0
 */
public class ContactMapper {

  private ContactMapper() {}

  /**
   * Converts a {@link Contact} entity to a {@link ContactResponse} DTO.
   *
   * <p>This method takes a {@code Contact} object and maps its fields to a {@code ContactResponse} object.
   * If the input {@code Contact} is {@code null}, this method returns {@code null}.</p>
   *
   * @param contact the {@code Contact} entity to be converted.
   * @return the corresponding {@code ContactResponse} DTO, or {@code null} if the input is {@code null}.
   */
  public static ContactResponse toContactResponse(final Contact contact) {
    if (nonNull(contact)) {
      return ContactResponse.builder()
          .id(contact.getContactId())
          .contactType(contact.getContactType())
          .contact(contact.getContact())
          .createdOn(contact.getCreatedOn())
          .updatedOn(contact.getUpdatedOn())
          .build();
    }
    return null;
  }

  /**
   * Converts a list of {@link Contact} entities to a list of {@link ContactResponse} DTOs.
   *
   * <p>This method takes a list of {@code Contact} objects, maps each to a {@code ContactResponse} object,
   * and returns a list of these DTOs. If the input list is {@code null} or empty, this method returns an empty list.</p>
   *
   * @param entries the list of {@code Contact} entities to be converted.
   * @return the list of corresponding {@code ContactResponse} DTOs, or an empty list if the input is {@code null} or empty.
   */
  public static List<ContactResponse> toContactResponses(final List<Contact> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .map(ContactMapper::toContactResponse)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
    return emptyList();
  }
}
