package com.fleencorp.feen.service.user;


import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import com.fleencorp.feen.model.response.user.profile.RetrieveMemberInfoResponse;
import com.fleencorp.feen.model.response.user.profile.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.model.response.user.profile.RetrieveProfileStatusResponse;
import com.fleencorp.feen.model.security.FleenUser;


public interface MemberService {

  boolean isIdExists(Long memberId);

  boolean isUsernameExist(String username);

  Member findMember(Long memberId) throws MemberNotFoundException;

  RetrieveMemberInfoResponse getMemberInfo(FleenUser user);

  RetrieveMemberUpdateInfoResponse getMemberUpdateInfo(FleenUser user);

  RetrieveProfileStatusResponse getProfileStatus(FleenUser user);

  EntityExistsResponse verifyMemberEmailAddressExists(String emailAddress);

  EntityExistsResponse verifyMemberPhoneNumberExists(String phoneNumber);

  void clearAuthenticationTokens(String username);

}
