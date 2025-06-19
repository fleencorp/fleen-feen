package com.fleencorp.feen.contact.mapper;

import com.fleencorp.feen.contact.model.domain.Contact;
import com.fleencorp.feen.contact.model.info.ContactRequestEligibilityInfo;
import com.fleencorp.feen.contact.model.response.base.ContactResponse;

import java.util.List;

public interface ContactMapper {

  ContactRequestEligibilityInfo toEligibilityInfo(boolean eligible);

  ContactResponse toContactResponse(Contact entry);

  List<ContactResponse> toContactResponses(List<Contact> entries);
}
