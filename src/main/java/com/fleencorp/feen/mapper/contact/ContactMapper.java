package com.fleencorp.feen.mapper.contact;

import com.fleencorp.feen.model.domain.social.Contact;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.model.response.social.contact.ContactResponse;

import java.util.List;

public interface ContactMapper {

  ContactRequestEligibilityInfo toEligibilityInfo(boolean eligible);

  ContactResponse toContactResponse(Contact entry);

  List<ContactResponse> toContactResponses(List<Contact> entries);
}
