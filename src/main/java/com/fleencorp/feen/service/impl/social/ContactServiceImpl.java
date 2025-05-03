package com.fleencorp.feen.service.impl.social;

import com.fleencorp.feen.exception.social.contact.ContactNotFoundException;
import com.fleencorp.feen.mapper.contact.ContactMapper;
import com.fleencorp.feen.model.domain.social.Contact;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.social.contact.AddContactDto;
import com.fleencorp.feen.model.dto.social.contact.UpdateContactDto;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.model.request.search.social.ContactSearchRequest;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.model.response.social.contact.AddContactResponse;
import com.fleencorp.feen.model.response.social.contact.ContactResponse;
import com.fleencorp.feen.model.response.social.contact.DeleteContactResponse;
import com.fleencorp.feen.model.response.social.contact.UpdateContactResponse;
import com.fleencorp.feen.model.search.contact.ContactSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.social.ContactRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.social.BlockUserService;
import com.fleencorp.feen.service.social.ContactService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;

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
  private final StreamService streamService;
  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ContactServiceImpl} instance with the specified contact repository.
   *
   * @param contactRepository the repository for performing CRUD operations on Contact entities.
   */
  public ContactServiceImpl(
      final ChatSpaceService chatSpaceService,
      final BlockUserService blockUserService,
      final StreamService streamService,
      final ContactRepository contactRepository,
      final ContactMapper contactMapper,
      final Localizer localizer) {
    this.chatSpaceService = chatSpaceService;
    this.blockUserService = blockUserService;
    this.streamService = streamService;
    this.contactRepository = contactRepository;
    this.contactMapper = contactMapper;
    this.localizer = localizer;
  }

  /**
   * Finds contacts for the specified user based on the provided search criteria.
   *
   * <p>This method retrieves a paginated list of contacts owned by the user according to the
   * search request parameters. It converts the retrieved contacts into a list of contact responses
   * and returns a search result view containing the results and pagination information.</p>
   *
   * @param searchRequest the request containing search criteria and pagination information.
   * @param user the user whose contacts are to be retrieved.
   * @return a ContactSearchResult containing search result view and containing the list of contact responses and pagination details.
   */
  @Override
  public ContactSearchResult findContacts(final ContactSearchRequest searchRequest, final FleenUser user) {
    // Retrieve a page of contacts owned by the user according to the search request
    final Page<Contact> page = contactRepository.findByOwner(user.toMember(), searchRequest.getPage());
    // Convert the retrieved contacts into a list of contact responses
    final List<ContactResponse> contactResponses = contactMapper.toContactResponses(page.getContent());
    // Create the search result
    final ContactSearchResult contactSearchResult = ContactSearchResult.of(toSearchResult(contactResponses, page));
    // Return a search result view with the contact responses and pagination details
    return localizer.of(contactSearchResult);
  }

  /**
   * Adds or updates a contact for the given user.
   *
   * <p>This method first checks if a contact with the specified type already exists for the user.
   * If the contact exists, it updates the existing contact with the new details from the DTO.
   * Otherwise, it creates a new contact based on the details from the DTO.
   * Finally, the contact is saved to the repository and a response object is returned.</p>
   *
   * @param addContactDto the DTO containing the contact details to be added or updated.
   * @param user the user for whom the contact is being added or updated.
   * @return an {@link AddContactResponse} representing the added or updated contact.
   */
  @Override
  public AddContactResponse addContact(final AddContactDto addContactDto, final FleenUser user) {
    // Find existing contact by owner and contact type
    final Contact contact = contactRepository.findByOwnerAndContactType(user.toMember(), addContactDto.getContactType())
      .map(existingContact -> {
        // If contact exists, update it with the new details
        existingContact.update(addContactDto.getContactType(), addContactDto.getContact());
        return existingContact;
      })
      // If contact does not exist, create a new contact
      .orElseGet(() -> addContactDto.toContact(user.toMember()));

    // Save the contact to the repository
    contactRepository.save(contact);
    // Convert the contact to a  response
    final ContactResponse contactResponse = contactMapper.toContactResponse(contact);
    // Create the response
    final AddContactResponse addContactResponse = AddContactResponse.of(contactResponse);
    // Convert the contact to a response object and return it
    return localizer.of(addContactResponse);
  }

  /**
   * Updates an existing contact for the given user.
   *
   * <p>This method first retrieves the contact by its ID. If the contact is not found,
   * it throws a ContactNotFoundException. It then verifies that the user is the owner of the contact.
   * If the verification passes, the contact is updated with the new details from the DTO,
   * saved to the repository, and a response object is returned.</p>
   *
   * @param contactId the ID of the contact to be updated.
   * @param updateContactDto the DTO containing the updated contact details.
   * @param user the user who owns the contact.
   * @return an {@link UpdateContactResponse} representing the updated contact.
   */
  @Override
  public UpdateContactResponse updateContact(final Long contactId, final UpdateContactDto updateContactDto, final FleenUser user) {
    // Retrieve the contact by ID, or throw an exception if not found
    final Contact contact = contactRepository.findByContactIdAndOwner(contactId, user.toMember())
      .map(existingContact -> {
        // Update the contact with new details from the DTO
        existingContact.update(updateContactDto.getContactType(), updateContactDto.getContact());
        return existingContact;
      })
      .orElseThrow(ContactNotFoundException.of(contactId));

    // Save the updated contact to the repository
    contactRepository.save(contact);
    // Convert the contact to a  response
    final ContactResponse contactResponse = contactMapper.toContactResponse(contact);
    // Create the response
    final UpdateContactResponse updateContactResponse = UpdateContactResponse.of(contactResponse);
    // Convert the contact to a response object and return it
    return localizer.of(updateContactResponse);
  }

  /**
   * Deletes an existing contact for the given user.
   *
   * <p>This method retrieves the contact by its ID and verifies that it belongs to the user.
   * If the contact is not found, it throws a ContactNotFoundException.
   * If the contact is found and belongs to the user, it is deleted from the repository,
   * and a response object is returned.</p>
   *
   * @param contactId the ID of the contact to be deleted.
   * @param user the user who owns the contact.
   * @return a DeleteContactResponse representing the deletion response.
   */
  @Override
  public DeleteContactResponse deleteContact(final Long contactId, final FleenUser user) {
    // Retrieve the contact by ID and owner, or throw an exception if not found
    final Contact contact = contactRepository.findByContactIdAndOwner(contactId, user.toMember())
      .orElseThrow(ContactNotFoundException.of(contactId));
    // Delete the contact from the repository
    contactRepository.delete(contact);
    // Return a response object indicating successful deletion
    return localizer.of(DeleteContactResponse.of());
  }

  /**
   * Deletes all contacts belonging to the specified user.
   *
   * <p>This method removes all contacts associated with the given user from the repository.
   * It does not return any specific data related to the deletion but confirms the action
   * by returning a response object.</p>
   *
   * @param user the user whose contacts are to be deleted.
   * @return an object representing the deletion response.
   */
  @Override
  public DeleteResponse deleteAllContact(final FleenUser user) {
    // Delete all contacts associated with the user
    contactRepository.deleteAllByOwner(user.toMember());
    // Return a response object indicating successful deletion
    return localizer.of(DeleteResponse.of());
  }

  @Override
  public ContactRequestEligibilityInfo checkContactRequestEligibility(final Member viewer, final Member target) {
    final boolean attendedStreamTogether = streamService.existsByAttendees(viewer, target);
    final boolean inSameChatSpace = chatSpaceService.existsByMembers(viewer, target);
    final boolean isBlocked = blockUserService.existsByInitiatorAndRecipient(viewer, target);
    final boolean hasContacts = contactRepository.countByOwner(target) > 0;

    final boolean eligible = (attendedStreamTogether || inSameChatSpace) && !isBlocked && hasContacts;
    return contactMapper.toEligibilityInfo(eligible);
  }

}
