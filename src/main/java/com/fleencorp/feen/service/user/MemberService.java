package com.fleencorp.feen.service.user;

import com.fleencorp.feen.model.response.other.EntityExistsResponse;

public interface MemberService {

  boolean isIdExists(Long memberId);

  EntityExistsResponse isMemberEmailAddressExists(String emailAddress);

  EntityExistsResponse isMemberPhoneNumberExists(String phoneNumber);
}
