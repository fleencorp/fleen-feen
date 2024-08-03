package com.fleencorp.feen.service.share;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.dto.share.contact.AddContactDto;
import com.fleencorp.feen.model.dto.share.contact.UpdateContactDto;
import com.fleencorp.feen.model.request.search.share.ContactSearchRequest;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.model.response.share.contact.AddContactResponse;
import com.fleencorp.feen.model.response.share.contact.UpdateContactResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface ContactService {

  SearchResultView findContacts(ContactSearchRequest searchRequest, FleenUser user);

  AddContactResponse addContact(AddContactDto dto, FleenUser user);

  UpdateContactResponse updateContact(Long contactId, UpdateContactDto updateContactDto, FleenUser user);

  DeleteResponse deleteContact(Long contactId, FleenUser user);

  DeleteResponse deleteAllContact(FleenUser user);
}
