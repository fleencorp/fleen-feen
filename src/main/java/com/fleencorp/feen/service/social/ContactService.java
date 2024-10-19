package com.fleencorp.feen.service.social;

import com.fleencorp.feen.model.dto.social.contact.AddContactDto;
import com.fleencorp.feen.model.dto.social.contact.UpdateContactDto;
import com.fleencorp.feen.model.request.search.social.ContactSearchRequest;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.model.response.social.contact.AddContactResponse;
import com.fleencorp.feen.model.response.social.contact.DeleteContactResponse;
import com.fleencorp.feen.model.response.social.contact.UpdateContactResponse;
import com.fleencorp.feen.model.search.contact.ContactSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ContactService {

  ContactSearchResult findContacts(ContactSearchRequest searchRequest, FleenUser user);

  AddContactResponse addContact(AddContactDto dto, FleenUser user);

  UpdateContactResponse updateContact(Long contactId, UpdateContactDto updateContactDto, FleenUser user);

  DeleteContactResponse deleteContact(Long contactId, FleenUser user);

  DeleteResponse deleteAllContact(FleenUser user);
}
