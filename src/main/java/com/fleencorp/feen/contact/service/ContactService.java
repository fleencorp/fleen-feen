package com.fleencorp.feen.contact.service;

import com.fleencorp.feen.contact.model.dto.DeleteContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactDto;
import com.fleencorp.feen.contact.model.dto.UpdateContactSingleDto;
import com.fleencorp.feen.contact.model.request.ContactSearchRequest;
import com.fleencorp.feen.contact.model.response.ContactDeleteResponse;
import com.fleencorp.feen.contact.model.response.ContactUpdateResponse;
import com.fleencorp.feen.contact.model.response.GetAvailableContactTypeResponse;
import com.fleencorp.feen.contact.model.search.ContactSearchResult;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface ContactService {

  GetAvailableContactTypeResponse getAvailableContactTypes();

  ContactSearchResult findContacts(ContactSearchRequest searchRequest, RegisteredUser user);

  ContactUpdateResponse updateContact(UpdateContactSingleDto updateContactDto, RegisteredUser user);

  ContactUpdateResponse updateContacts(UpdateContactDto updateContactDto, RegisteredUser user);

  ContactDeleteResponse deleteContact(DeleteContactDto deleteContactDto, RegisteredUser user);

  ContactRequestEligibilityInfo checkContactRequestEligibility(Member viewer, Member target);
}
