package com.fleencorp.feen.mapper.impl.social;

import com.fleencorp.feen.constant.user.IsEligibleForContactRequest;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.social.ContactMapper;
import com.fleencorp.feen.model.domain.social.Contact;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.model.response.social.contact.ContactResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

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
@Component
public final class ContactMapperImpl extends BaseMapper implements ContactMapper {

  public ContactMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

  /**
   * Converts a boolean eligibility flag into a {@link ContactRequestEligibilityInfo} object.
   *
   * <p>It determines the eligibility status and translates associated message codes into localized messages.
   * These messages explain whether the user is eligible to send a contact request.</p>
   *
   * @param eligible {@code true} if the user is eligible to send a contact request; {@code false} otherwise
   * @return a {@link ContactRequestEligibilityInfo} containing the eligibility status and localized messages
   */

  @Override
  public ContactRequestEligibilityInfo toEligibilityInfo(final boolean eligible) {
    final IsEligibleForContactRequest isEligibleForContactRequest = IsEligibleForContactRequest.by(eligible);

    return ContactRequestEligibilityInfo.of(
      eligible,
      translate(isEligibleForContactRequest.getMessageCode()),
      translate(isEligibleForContactRequest.getMessageCode2()),
      translate(isEligibleForContactRequest.getMessageCode3())
    );
  }

  /**
   * Converts a {@link Contact} entity to a {@link ContactResponse} DTO.
   *
   * <p>This method takes a {@code Contact} object and maps its fields to a {@code ContactResponse} object.
   * If the input {@code Contact} is {@code null}, this method returns {@code null}.</p>
   *
   * @param entry the {@code Contact} entity to be converted.
   * @return the corresponding {@code ContactResponse} DTO, or {@code null} if the input is {@code null}.
   */
  @Override
  public ContactResponse toContactResponse(final Contact entry) {
    if (nonNull(entry)) {
      final ContactResponse response = new ContactResponse();

      response.setId(entry.getContactId());
      response.setContactType(entry.getContactType());
      response.setContactTypeLabel(entry.getContactLabel());
      response.setContact(entry.getContactValue());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      return response;
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
  @Override
  public List<ContactResponse> toContactResponses(final List<Contact> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(this::toContactResponse)
          .toList();
    }
    return List.of();
  }
}
