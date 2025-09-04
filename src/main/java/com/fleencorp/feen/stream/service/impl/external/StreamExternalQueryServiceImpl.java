package com.fleencorp.feen.stream.service.impl.external;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.calendar.contract.IsACalendar;
import com.fleencorp.feen.shared.calendar.service.CalendarQueryService;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpaceMember;
import com.fleencorp.feen.shared.chat.space.service.ChatSpaceQueryService;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.member.service.MemberQueryService;
import com.fleencorp.feen.stream.service.external.StreamExternalQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StreamExternalQueryServiceImpl implements StreamExternalQueryService {

  private final CalendarQueryService calendarQueryService;
  private final ChatSpaceQueryService chatSpaceQueryService;
  private final MemberQueryService memberQueryService;

  public StreamExternalQueryServiceImpl(
      final CalendarQueryService calendarQueryService,
      final ChatSpaceQueryService chatSpaceQueryService,
      final MemberQueryService memberQueryService) {
    this.calendarQueryService = calendarQueryService;
    this.chatSpaceQueryService = chatSpaceQueryService;
    this.memberQueryService = memberQueryService;
  }

  @Override
  public Optional<IsAChatSpaceMember> findByChatSpaceAndMemberAndStatus(Long chatSpaceId, Long memberId, ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    return chatSpaceQueryService.findByChatSpaceAndMemberAndStatus(chatSpaceId, memberId, requestToJoinStatus);
  }

  @Override
  public Optional<IsAMember> findByEmailAddress(String emailAddress) {
    return memberQueryService.findByEmailAddress(emailAddress);
  }

  @Override
  public Optional<IsAMember> findMemberById(Long memberId) {
    return memberQueryService.findMemberById(memberId);
  }

  @Override
  public IsACalendar findCalendar(String code) {
    return calendarQueryService.findDistinctByCodeIgnoreCase(code)
      .orElseThrow(FailedOperationException::new);
  }
}
