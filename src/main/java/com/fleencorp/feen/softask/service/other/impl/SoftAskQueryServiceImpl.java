package com.fleencorp.feen.softask.service.other.impl;

import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.chat.space.service.ChatSpaceQueryService;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.member.service.MemberQueryService;
import com.fleencorp.feen.shared.poll.contract.IsAPoll;
import com.fleencorp.feen.shared.poll.service.PollQueryService;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.service.StreamQueryService;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.exception.core.SoftAskParentNotFoundException;
import com.fleencorp.feen.softask.service.other.SoftAskQueryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SoftAskQueryServiceImpl implements SoftAskQueryService {

  private final ChatSpaceQueryService chatSpaceQueryService;
  private final MemberQueryService memberQueryService;
  private final PollQueryService pollQueryService;
  private final StreamQueryService streamQueryService;

  public SoftAskQueryServiceImpl(
      final ChatSpaceQueryService chatSpaceQueryService,
      final MemberQueryService memberQueryService,
      final PollQueryService pollQueryService,
      @Qualifier("sharedStreamQueryService") final StreamQueryService streamQueryService) {
    this.chatSpaceQueryService = chatSpaceQueryService;
    this.memberQueryService = memberQueryService;
    this.pollQueryService = pollQueryService;
    this.streamQueryService = streamQueryService;
  }

  @Override
  public IsAChatSpace findChatSpaceOrThrow(final Long chatSpaceId) {
    return chatSpaceQueryService.findChatSpaceById(chatSpaceId)
      .orElseThrow(SoftAskParentNotFoundException.of(SoftAskParentType.CHAT_SPACE, chatSpaceId));
  }

  @Override
  public IsAPoll findPollOrThrow(final Long pollId) {
    return pollQueryService.findPollById(pollId)
      .orElseThrow(SoftAskParentNotFoundException.of(SoftAskParentType.POLL, pollId));
  }

  @Override
  public IsAStream findStreamOrThrow(final Long streamId) {
    return streamQueryService.findStreamById(streamId)
      .orElseThrow(SoftAskParentNotFoundException.of(SoftAskParentType.STREAM, streamId));
  }

  @Override
  public IsAMember findMemberOrThrow(final Long memberId) {
    return memberQueryService.findMemberOrThrow(memberId);
  }
}
