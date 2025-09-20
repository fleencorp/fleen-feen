package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.poll.mapper.PollUnifiedMapper;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollExternalQueryService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollSearchService;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;

@Service
public class PollExternalQueryServiceImpl implements PollExternalQueryService {

  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final MemberService memberService;
  private final StreamOperationsService streamOperationsService;
  private final PollCommonService pollCommonService;
  private final PollOperationsService pollOperationsService;
  private final PollSearchService pollSearchService;
  private final PollUnifiedMapper pollUnifiedMapper;
  private final Localizer localizer;

  public PollExternalQueryServiceImpl(
    final ChatSpaceOperationsService chatSpaceOperationsService,
    final MemberService memberService,
    final PollCommonService pollCommonService,
    final PollOperationsService pollOperationsService,
    final PollSearchService pollSearchService,
    final StreamOperationsService streamOperationsService,
    final PollUnifiedMapper pollUnifiedMapper,
    final Localizer localizer) {
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.memberService = memberService;
    this.pollCommonService = pollCommonService;
    this.pollOperationsService = pollOperationsService;
    this.pollSearchService = pollSearchService;
    this.streamOperationsService = streamOperationsService;
    this.pollUnifiedMapper = pollUnifiedMapper;
    this.localizer = localizer;
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
