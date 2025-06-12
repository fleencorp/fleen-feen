package com.fleencorp.feen.contact.controller;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.contact.model.dto.DeleteContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactSingleDto;
import com.fleencorp.feen.contact.model.request.ContactSearchRequest;
import com.fleencorp.feen.contact.model.response.ContactDeleteResponse;
import com.fleencorp.feen.contact.model.response.ContactUpdateResponse;
import com.fleencorp.feen.contact.model.response.GetAvailableContactTypeResponse;
import com.fleencorp.feen.contact.model.search.ContactSearchResult;
import com.fleencorp.feen.contact.service.ContactService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
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
      @AuthenticationPrincipal final RegisteredUser user) {
    return contactService.findContacts(contactSearchRequest, user);
  }

  @PutMapping(value = "/update")
  public ContactUpdateResponse updateContact(
      @Valid @RequestBody final UpdateContactSingleDto updateContactDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return contactService.updateContact(updateContactDto, user);
  }

  @PutMapping(value = "/update-contacts")
  public ContactUpdateResponse updateContact(
      @Valid @RequestBody final UpdateContactDto updateContactDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return contactService.updateContacts(updateContactDto, user);
  }

  @PutMapping(value = "/delete")
  public ContactDeleteResponse deleteContact(
      @Valid @RequestBody final DeleteContactDto deleteContactDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return contactService.deleteContact(deleteContactDto, user);
  }
}
