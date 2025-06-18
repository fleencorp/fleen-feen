package com.fleencorp.feen.user.service.member;


import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.response.common.EmailAddressExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberExistsResponse;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveProfileStatusResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;


public interface MemberService {

  boolean isIdExists(Long memberId);

  boolean isUsernameExist(String username);

  Member findMember(Long memberId) throws MemberNotFoundException;

  RetrieveMemberInfoResponse getMemberInfo(RegisteredUser user) throws FailedOperationException;

  RetrieveMemberUpdateInfoResponse getMemberUpdateInfo(RegisteredUser user) throws FailedOperationException;

  RetrieveProfileStatusResponse getProfileStatus(RegisteredUser user) throws FailedOperationException;

  EmailAddressExistsResponse verifyMemberEmailAddressExists(String emailAddress);

  PhoneNumberExistsResponse verifyMemberPhoneNumberExists(String phoneNumber);

  void clearAuthenticationTokens(String username);

}
