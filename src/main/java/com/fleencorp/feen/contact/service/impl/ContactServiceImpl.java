package com.fleencorp.feen.contact.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.block.user.service.BlockUserService;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.contact.constant.ContactType;
import com.fleencorp.feen.contact.mapper.ContactMapper;
import com.fleencorp.feen.contact.model.domain.Contact;
import com.fleencorp.feen.contact.model.dto.DeleteContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactSingleDto;
import com.fleencorp.feen.contact.model.info.ContactRequestEligibilityInfo;
import com.fleencorp.feen.contact.model.info.ContactTypeInfo;
import com.fleencorp.feen.contact.model.request.ContactSearchRequest;
import com.fleencorp.feen.contact.model.response.ContactDeleteResponse;
import com.fleencorp.feen.contact.model.response.ContactUpdateResponse;
import com.fleencorp.feen.contact.model.response.GetAvailableContactTypeResponse;
import com.fleencorp.feen.contact.model.response.base.ContactResponse;
import com.fleencorp.feen.contact.model.search.ContactSearchResult;
import com.fleencorp.feen.contact.repository.ContactRepository;
import com.fleencorp.feen.contact.service.ContactService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static com.fleencorp.feen.contact.model.dto.UpdateContactDto.ContactDto;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link ContactService} interface for managing contacts.
 *
 * <p>This service provides methods for CRUD operations on contacts, such as adding, updating,
 * deleting, and searching for contacts. It relies on the {@link ContactRepository} for data
 * persistence and retrieval.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class ContactServiceImpl implements ContactService {

  private final ChatSpaceService chatSpaceService;
  private final BlockUserService blockUserService;
  private final StreamOperationsService streamOperationsService;
  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ContactServiceImpl}, which handles contact-related features such as retrieving, mapping, and filtering contacts.
   *
   * @param chatSpaceService the service for managing chat space data, used to resolve context for contact relationships
   * @param blockUserService the service for managing blocked user relationships
   * @param streamOperationsService the service for handling operations related to streams associated with contacts
   * @param contactRepository the repository interface for persisting and retrieving contact entities
   * @param contactMapper the mapper used for converting contact entities to DTOs and vice versa
   * @param localizer the component for resolving localized messages for user-facing responses
   */
  public ContactServiceImpl(
      final ChatSpaceService chatSpaceService,
      final BlockUserService blockUserService,
      final StreamOperationsService streamOperationsService,
      final ContactRepository contactRepository,
      final ContactMapper contactMapper,
      final Localizer localizer) {
    this.chatSpaceService = chatSpaceService;
    this.blockUserService = blockUserService;
    this.streamOperationsService = streamOperationsService;
    this.contactRepository = contactRepository;
    this.contactMapper = contactMapper;
    this.localizer = localizer;
  }

  @Override
  @Cacheable("availableContactTypes")
  public GetAvailableContactTypeResponse getAvailableContactTypes() {
    final Map<ContactType, ContactTypeInfo> availableContactTypes =
      Stream.of(ContactType.values())
        .collect(Collectors.collectingAndThen(
          Collectors.toMap(
            ct -> ct,
            ct -> ContactTypeInfo.of(ct, ct.getValue(), ct.getFormat()
            ),
            (_, b) -> b,
            () -> new EnumMap<>(ContactType.class)
          ),
          Map::copyOf
        ));

    // Create the response
    final GetAvailableContactTypeResponse getAvailableContactTypeResponse = GetAvailableContactTypeResponse.of(availableContactTypes);
    // Return the response
    return localizer.of(getAvailableContactTypeResponse);
  }

  /**
   * Finds contacts for the specified user based on the provided search criteria.
   *
   * <p>This method retrieves a paginated list of contacts owned by the user according to the
   * search request parameters. It converts the retrieved contacts into a list of contact responses
   * and returns a search result containing the results and pagination information.</p>
   *
   * @param searchRequest the request containing search criteria and pagination information.
   * @param user the user whose contacts are to be retrieved.
   * @return a ContactSearchResult containing search result and containing the list of contact responses and pagination details.
   */
  @Override
  public ContactSearchResult findContacts(final ContactSearchRequest searchRequest, final RegisteredUser user) {
    final Member member = user.toMember();
    final Pageable pageable = searchRequest.getPage();

    // Retrieve a page of contacts owned by the user according to the search request
    final Page<Contact> page = contactRepository.findByOwner(member, pageable);
    // Convert the retrieved contacts into a list of contact responses
    final List<ContactResponse> contactResponses = contactMapper.toContactResponses(page.getContent());
    // Process other contact details
    processContactDetails(contactResponses);
    // Create Search result
    final SearchResult searchResult = toSearchResult(contactResponses, page);
    // Create the contact search result
    final ContactSearchResult contactSearchResult = ContactSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(contactSearchResult);
  }

  /**
   * Processes the given contact response collection by marking each contact
   * as updatable based on its author ID.
   *
   * <p>If the collection is not null or empty, each non-null contact response is passed
   * to the {@code setEntityUpdatableByUser} method using its author ID to determine
   * if the contact can be updated by the user.</p>
   *
   * @param contactResponses the collection of contact responses to process
   */
  protected void processContactDetails(final Collection<ContactResponse> contactResponses) {
    if (nonNull(contactResponses) && !contactResponses.isEmpty()) {
      contactResponses.stream()
        .filter(Objects::nonNull)
        .forEach(contactResponse -> setEntityUpdatableByUser(contactResponse, contactResponse.getAuthorId()));
    }
  }

  /**
   * Updates a single contact for the given user and returns a localized update response.
   *
   * <p>If a contact with the specified type already exists, it is updated only if the value has changed.
   * If no such contact exists, a new one is created and saved. The resulting contact is then mapped
   * to a response DTO, wrapped in a localized response, and returned.</p>
   *
   * @param updateContactDto the DTO containing the contact type and new value
   * @param user the registered user whose contact is to be updated or created
   * @return a localized response containing the updated contact information
   */
  @Override
  @Transactional
  public ContactUpdateResponse updateContact(final UpdateContactSingleDto updateContactDto, final RegisteredUser user) {
    // Update the contact if possible
    final Contact contact = resolveContactToSaveIfNewOrChanged(updateContactDto, user.toMember());
    // Convert the contact to a  response
    final ContactResponse contactResponse = contactMapper.toContactResponse(contact);
    // Create the response
    final ContactUpdateResponse contactUpdateResponse = ContactUpdateResponse.of(contactResponse);
    // Convert the contact to a response object and return it
    return localizer.of(contactUpdateResponse);
  }

  /**
   * Resolves a contact to be returned after applying an update if needed.
   *
   * <p>If a contact with the given type already exists for the member and its value has changed,
   * it is updated and saved. If the existing value is unchanged, it is returned as-is without persisting.
   * If no such contact exists, a new contact is created, saved, and returned.</p>
   *
   * @param dto the contact update request containing the contact type and new value
   * @param member the owner of the contact
   * @return the up-to-date contact after any necessary creation or update
   */
  protected Contact resolveContactToSaveIfNewOrChanged(final UpdateContactSingleDto dto, final Member member) {
    final ContactType contactType = dto.getContactType();
    final String newContactValue = dto.getContact();

    return contactRepository.findByContactTypeAndOwner(contactType, member)
      .map(existing -> {
        if (existing.isChanged(newContactValue)) {
          existing.update(contactType, newContactValue);
          return contactRepository.save(existing);
        }

        return existing;
      })
      .orElseGet(() -> {
        final Contact newContact = dto.toContact(member);
        return contactRepository.save(newContact);
    });
  }

  /**
   * Updates the contacts of the given user based on the provided contact update request.
   *
   * <p>The method converts the {@code FleenUser} to its corresponding {@code Member} representation,
   * retrieves the member's existing contacts, and maps them by contact type.
   * It then updates existing contacts or creates new ones using the valid contact DTOs from the request.
   * Contacts that are no longer present in the incoming update are removed.
   * A localized response is returned to indicate the outcome of the update process.</p>
   *
   * @param updateContactDto the DTO containing contact information to be updated
   * @param user the user whose contacts are being updated
   * @return a localized response indicating the result of the contact update
   */
  @Override
  @Transactional
  public ContactUpdateResponse updateContacts(final UpdateContactDto updateContactDto, final RegisteredUser user) {
    final Member member = user.toMember();

    final List<Contact> existingContacts = contactRepository.findByOwner(member);
    final Map<ContactType, Contact> existingContactMap = groupContactsByType(existingContacts);
    final List<ContactDto> validContacts = updateContactDto.getContacts();

    upsertContacts(validContacts, existingContactMap, member);
    removeStaleContacts(existingContacts, updateContactDto.getValidContactTypes());

    final ContactUpdateResponse contactUpdateResponse = ContactUpdateResponse.of();
    return localizer.of(contactUpdateResponse);
  }

  /**
   * Groups a list of contacts by their contact type.
   *
   * <p>Each contact type is expected to be unique within the list. If duplicates are present,
   * an exception will be thrown. This method is useful for fast lookup and upsert operations.</p>
   *
   * @param contacts the list of contacts to group
   * @return a map where the key is the contact type and the value is the corresponding contact
   */
  protected Map<ContactType, Contact> groupContactsByType(final List<Contact> contacts) {
    return contacts.stream()
      .collect(Collectors.toMap(
        Contact::getContactType,
        Function.identity()
    ));
  }

  /**
   * Saves new or updated contact information based on the provided DTOs.
   *
   * <p>For each valid contact DTO, this method determines whether the contact already exists
   * and whether its value has changed. If the contact is new or its value differs from the existing one,
   * it is prepared for persistence. All such contacts are saved in a single batch operation.
   * Invalid DTOs are skipped.</p>
   *
   * @param contactDtos the collection of contact DTOs to process
   * @param existingContactMap a map of the user's existing contacts keyed by contact type
   * @param user the member to associate with any new contacts
   */
  protected void upsertContacts(final Collection<ContactDto> contactDtos, final Map<ContactType, Contact> existingContactMap, final Member user) {
    final List<Contact> contactsToSave = new ArrayList<>();

    for (final ContactDto dto : contactDtos) {
      if (dto.isInvalid()) {
        continue;
      }

      resolveContactToSaveIfNewOrChanged(dto, existingContactMap, user)
        .ifPresent(contactsToSave::add);
    }

    if (!contactsToSave.isEmpty()) {
      contactRepository.saveAll(contactsToSave);
    }
  }

  /**
   * Determines whether a contact should be saved based on the provided DTO.
   *
   * <p>If a contact with the same type already exists in the {@code existingContactMap}
   * and its value has changed, the existing contact is updated and returned.
   * If the contact type does not exist, a new contact is created and returned.
   * If the contact exists and its value has not changed, an empty {@code Optional} is returned.</p>
   *
   * @param dto the incoming contact DTO
   * @param existingContactMap a map of existing contacts keyed by their contact type
   * @param user the member to associate with a new contact, if needed
   * @return an {@code Optional} containing a contact to save if new or modified; otherwise, {@code Optional.empty()}
   */
  protected Optional<Contact> resolveContactToSaveIfNewOrChanged(final ContactDto dto, final Map<ContactType, Contact> existingContactMap, final Member user) {
    final ContactType contactType = dto.getContactType();
    final String newContactValue = dto.getContact();

    if (existingContactMap.containsKey(contactType)) {
      final Contact existing = existingContactMap.get(contactType);

      if (existing.isChanged(newContactValue)) {
        existing.update(contactType, newContactValue);
        return Optional.of(existing);
      }

      return Optional.empty();
    } else {
      final Contact newContact = dto.toContact(user);
      return Optional.of(newContact);
    }
  }

  /**
   * Removes contacts from the provided list that have contact types
   * not present in the given incoming collection of types.
   *
   * <p>This method is typically used during updates to ensure that
   * stale or outdated contacts are removed from persistence if they
   * no longer appear in the incoming data.</p>
   *
   * @param existingContacts the list of current contacts to evaluate; may be modified
   * @param incomingTypes the collection of contact types that should be retained
   */
  protected void removeStaleContacts(final List<Contact> existingContacts, final Collection<ContactType> incomingTypes) {
    if (nonNull(existingContacts) && nonNull(incomingTypes)) {
      // Remove contacts that are no longer present in the incoming update
      for (final Contact existing : existingContacts) {
        if (!incomingTypes.contains(existing.getContactType())) {
          contactRepository.delete(existing);
        }
      }
    }
  }

  /**
   * Deletes an existing contact for the given user.
   *
   * <p>This method retrieves the contact by its ID and verifies that it belongs to the user.
   * If the contact is not found, it throws a ContactNotFoundException.
   * If the contact is found and belongs to the user, it is deleted from the repository,
   * and a response object is returned.</p>
   *
   * @param deleteContactDto the dto of the contact to be deleted.
   * @param user the user who owns the contact.
   * @return a DeleteContactResponse representing the deletion response.
   */
  @Override
  @Transactional
  public ContactDeleteResponse deleteContact(final DeleteContactDto deleteContactDto, final RegisteredUser user) {
    // Find the contacts of the user
    final Collection<Contact> contacts = contactRepository.findByContactTypeAndOwner(user.getId(), deleteContactDto.getContactTypes());
    // Delete the contacts from the repository
    contactRepository.deleteAll(contacts);
    // Return a response object indicating successful deletion
    return localizer.of(ContactDeleteResponse.of());
  }

  /**
   * Determines whether the given viewer is eligible to request contact information from the target member.
   *
   * <p>The eligibility is based on the following conditions:
   * the viewer and target must have either attended a stream together or be part of the same chat space,
   * the viewer must not have blocked the target, and the target must have at least one contact available.</p>
   *
   * @param viewer the member attempting to request contact information
   * @param target the member whose contact information is being requested
   * @return an object containing the result of the eligibility check
   */
  @Override
  public ContactRequestEligibilityInfo checkContactRequestEligibility(final Member viewer, final Member target) {
    final boolean attendedStreamTogether = streamOperationsService.existsByAttendees(viewer, target);
    final boolean inSameChatSpace = chatSpaceService.existsByMembers(viewer, target);
    final boolean isBlocked = blockUserService.existsByInitiatorAndRecipient(viewer, target);
    final boolean hasContacts = contactRepository.countByOwner(target) > 0;

    final boolean eligible = (attendedStreamTogether || inSameChatSpace) && !isBlocked && hasContacts;
    return contactMapper.toEligibilityInfo(eligible);
  }

}
