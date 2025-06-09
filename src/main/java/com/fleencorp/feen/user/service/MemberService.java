package com.fleencorp.feen.user.service;


import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.user.exception.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.response.common.EmailAddressExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberExistsResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveProfileStatusResponse;
import com.fleencorp.feen.model.security.FleenUser;


public interface MemberService {

  boolean isIdExists(Long memberId);

  boolean isUsernameExist(String username);

  Member findMember(Long memberId) throws MemberNotFoundException;

  RetrieveMemberInfoResponse getMemberInfo(FleenUser user) throws FailedOperationException;

  RetrieveMemberUpdateInfoResponse getMemberUpdateInfo(FleenUser user) throws FailedOperationException;

  RetrieveProfileStatusResponse getProfileStatus(FleenUser user) throws FailedOperationException;

  EmailAddressExistsResponse verifyMemberEmailAddressExists(String emailAddress);

  PhoneNumberExistsResponse verifyMemberPhoneNumberExists(String phoneNumber);

  void clearAuthenticationTokens(String username);

}
