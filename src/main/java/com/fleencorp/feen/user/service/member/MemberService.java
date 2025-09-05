package com.fleencorp.feen.user.service.member;


import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.model.response.EmailAddressExistsResponse;
import com.fleencorp.feen.common.model.response.PhoneNumberExistsResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveProfileStatusResponse;


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
