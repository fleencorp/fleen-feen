package com.fleencorp.feen.contact.mapper;

import com.fleencorp.feen.contact.model.domain.Contact;
import com.fleencorp.feen.contact.model.response.base.ContactResponse;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;

import java.util.List;

public interface ContactMapper {

  ContactRequestEligibilityInfo toEligibilityInfo(boolean eligible);

  ContactResponse toContactResponse(Contact entry);

  List<ContactResponse> toContactResponses(List<Contact> entries);
}
