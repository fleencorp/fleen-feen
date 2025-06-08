package com.fleencorp.feen.contact.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.contact.model.dto.AddContactDto;
import com.fleencorp.feen.contact.model.dto.DeleteContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactSingleDto;
import com.fleencorp.feen.contact.model.request.ContactSearchRequest;
import com.fleencorp.feen.contact.model.response.ContactAddResponse;
import com.fleencorp.feen.contact.model.response.ContactDeleteResponse;
import com.fleencorp.feen.contact.model.response.ContactUpdateResponse;
import com.fleencorp.feen.contact.model.response.GetAvailableContactTypeResponse;
import com.fleencorp.feen.contact.model.search.ContactSearchResult;
import com.fleencorp.feen.contact.service.ContactService;
import com.fleencorp.feen.model.security.FleenUser;
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

  @GetMapping(value = "/contact-types")
  public GetAvailableContactTypeResponse getAvailableContactTypes() {
    return contactService.getAvailableContactTypes();
  }

  @GetMapping(value = "/find-contacts")
  public ContactSearchResult findContacts(
      @SearchParam final ContactSearchRequest contactSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.findContacts(contactSearchRequest, user);
  }

  @PostMapping(value = "/add")
  public ContactAddResponse addContact(
      @Valid @RequestBody final AddContactDto addContactDto,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.addContact(addContactDto, user);
  }

  @PutMapping(value = "/update/{contactId}")
  public ContactUpdateResponse updateContact(
      @PathVariable(name = "contactId") final Long contactId,
      @Valid @RequestBody final UpdateContactSingleDto updateContactDto,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.updateContact(contactId, updateContactDto, user);
  }

  @PutMapping(value = "/update")
  public ContactUpdateResponse updateContact(
      @Valid @RequestBody final UpdateContactDto updateContactDto,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.updateContacts(updateContactDto, user);
  }

  @PutMapping(value = "/delete")
  public ContactDeleteResponse deleteContact(
      @Valid @RequestBody final DeleteContactDto deleteContactDto,
      @AuthenticationPrincipal final FleenUser user) {
    return contactService.deleteContact(deleteContactDto, user);
  }
}
