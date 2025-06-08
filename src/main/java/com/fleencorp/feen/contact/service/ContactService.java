package com.fleencorp.feen.contact.service;

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
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface ContactService {

  GetAvailableContactTypeResponse getAvailableContactTypes();

  ContactSearchResult findContacts(ContactSearchRequest searchRequest, FleenUser user);

  ContactAddResponse addContact(AddContactDto dto, FleenUser user);

  ContactUpdateResponse updateContact(Long contactId, UpdateContactSingleDto updateContactDto, FleenUser user);

  ContactUpdateResponse updateContacts(UpdateContactDto updateContactDto, FleenUser user);

  ContactDeleteResponse deleteContact(DeleteContactDto deleteContactDto, FleenUser user);

  DeleteResponse deleteAllContact(FleenUser user);

  ContactRequestEligibilityInfo checkContactRequestEligibility(Member viewer, Member target);
}
