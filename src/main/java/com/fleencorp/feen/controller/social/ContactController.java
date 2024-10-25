package com.fleencorp.feen.controller.social;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.social.contact.AddContactDto;
import com.fleencorp.feen.model.dto.social.contact.UpdateContactDto;
import com.fleencorp.feen.model.request.search.social.ContactSearchRequest;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.model.response.social.contact.AddContactResponse;
import com.fleencorp.feen.model.response.social.contact.DeleteContactResponse;
import com.fleencorp.feen.model.response.social.contact.UpdateContactResponse;
import com.fleencorp.feen.model.search.contact.ContactSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.social.ContactService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/contact")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class ContactController {

  private final ContactService contactService;

  public ContactController(final ContactService contactService) {
    this.contactService = contactService;
  }

  @GetMapping(value = "/find-contacts")
  public ContactSearchResult findContacts(
      @SearchParam final ContactSearchRequest contactSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.findContacts(contactSearchRequest, user);
  }

  @PostMapping(value = "/add")
  public AddContactResponse addContact(
      @Valid @RequestBody final AddContactDto addContactDto,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.addContact(addContactDto, user);
  }

  @PutMapping(value = "/update/{contactId}")
  public UpdateContactResponse updateContact(
      @PathVariable(name = "contactId") final Long contactId,
      @Valid @RequestBody final UpdateContactDto updateContactDto,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.updateContact(contactId, updateContactDto, user);
  }

  @DeleteMapping(value = "/delete/{contactId}")
  public DeleteContactResponse deleteContact(
      @PathVariable(name = "contactId") final Long contactId,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.deleteContact(contactId, user);
  }

  @DeleteMapping(value = "/delete-all")
  public DeleteResponse deleteAllContacts(
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.deleteAllContact(user);
  }
}
