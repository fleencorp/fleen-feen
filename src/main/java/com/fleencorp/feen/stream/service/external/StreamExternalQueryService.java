package com.fleencorp.feen.stream.service.external;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.shared.calendar.contract.IsACalendar;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpaceMember;
import com.fleencorp.feen.shared.member.contract.IsAMember;

import java.util.Optional;

public interface StreamExternalQueryService {

  Optional<IsAChatSpaceMember> findByChatSpaceAndMemberAndStatus(Long chatSpaceId, Long memberId, ChatSpaceRequestToJoinStatus requestToJoinStatus);

  Optional<IsAMember> findByEmailAddress(String emailAddress);

  Optional<IsAMember> findMemberById(Long memberId);

  IsACalendar findCalendar(String code);
}
