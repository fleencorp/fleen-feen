package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.poll.service.PollExternalQueryService;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.user.service.member.MemberService;
import org.springframework.stereotype.Service;

@Service
public class PollExternalQueryServiceImpl implements PollExternalQueryService {

  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final MemberService memberService;
  private final StreamOperationsService streamOperationsService;

  public PollExternalQueryServiceImpl(
    final ChatSpaceOperationsService chatSpaceOperationsService,
    final MemberService memberService,
    final StreamOperationsService streamOperationsService) {
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.memberService = memberService;
    this.streamOperationsService = streamOperationsService;
  }

  @Override
  public IsAMember findMemberById(Long memberId) {
    return memberService.findMember(memberId);
  }

  @Override
  public FleenStream findStreamById(final Long streamId) {
    return streamOperationsService.findStream(streamId);
  }

  @Override
  public ChatSpace findChatSpaceById(final Long chatSpaceId) {
    return chatSpaceOperationsService.findChatSpace(chatSpaceId);
  }

  @Override
  public void verifyCreatorOrAdminOfChatSpace(final IsAChatSpace chatSpace, final IsAMember member) {
    chatSpaceOperationsService.verifyCreatorOrAdminOfChatSpace(chatSpace, member);
  }
}
