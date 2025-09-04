package com.fleencorp.feen.shared.member.service;

import com.fleencorp.feen.shared.member.contract.IsAMember;

import java.util.Optional;

public interface MemberQueryService {

  Optional<IsAMember> findMemberById(Long memberId);

  Optional<IsAMember> findByEmailAddress(String emailAddress);

  IsAMember findMemberOrThrow(Long memberId);
}
