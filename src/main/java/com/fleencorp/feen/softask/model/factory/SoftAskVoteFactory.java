package com.fleencorp.feen.softask.model.factory;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
import com.fleencorp.feen.shared.common.util.ParentInfoUtil;

import java.util.Map;

import static java.util.Objects.isNull;

public final class SoftAskVoteFactory {

  private static final Map<SoftAskVoteParentType, VoteCreator> CREATORS = Map.of(
    SoftAskVoteParentType.SOFT_ASK, SoftAskVoteFactory::createSoftAskVote,
    SoftAskVoteParentType.SOFT_ASK_REPLY, SoftAskVoteFactory::createSoftAskReplyVote
  );

  private SoftAskVoteFactory() {}

  public static SoftAskVote toSoftAskVote(
      final SoftAskVoteDto dto,
      final IsAMember member,
      final SoftAsk softAsk,
      final SoftAskReply softAskReply) {

    checkParameters(dto, member);
    final VoteCreator creator = checkParentType(dto);

    return creator.create(dto, member, softAsk, softAskReply);
  }

  private static VoteCreator checkParentType(SoftAskVoteDto dto) {
    final VoteCreator creator = CREATORS.get(dto.getVoteParentType());

    if (creator == null) {
      throw FailedOperationException.of();
    }

    return creator;
  }

  private static void checkParameters(SoftAskVoteDto dto, IsAMember member) {
    if (isNull(dto) || isNull(member)) {
      throw FailedOperationException.of();
    }
  }

  private static SoftAskVote createSoftAskVote(SoftAskVoteDto dto, IsAMember member, SoftAsk softAsk, SoftAskReply unused) {
    final Long softAskId = softAsk.getId();

    final SoftAskVote vote = baseVote(dto, member, softAskId, softAsk);
    vote.setSoftAskId(softAskId);
    vote.setSoftAsk(softAsk);
    vote.setParentTitle(softAsk.getTitle());

    return vote;
  }

  private static SoftAskVote createSoftAskReplyVote(SoftAskVoteDto dto, IsAMember member, SoftAsk softAsk, SoftAskReply reply) {
    final Long softAskReplyId = reply.getSoftAskReplyId();

    final SoftAskVote vote = baseVote(dto, member, softAskReplyId, reply);
    vote.setSoftAskReplyId(softAskReplyId);
    vote.setSoftAskReply(reply);
    vote.setSoftAskId(softAsk.getSoftAskId());
    vote.setSoftAsk(softAsk);
    vote.setParentTitle(reply.getTitle());

    return vote;
  }

  private static SoftAskVote baseVote(SoftAskVoteDto dto, IsAMember member, Long parentId, SoftAskCommonData softAskCommonData) {
    final SoftAskVote vote = new SoftAskVote();
    vote.setParentId(parentId);
    vote.setParentType(dto.getVoteParentType());
    vote.setVoteType(dto.getVoteType());
    vote.setMemberId(member.getMemberId());

    final String parentSummary = ParentInfoUtil.getParentSummary(softAskCommonData.getSummary());
    vote.setParentSummary(parentSummary);

    return vote;
  }

  @FunctionalInterface
  private interface VoteCreator {

    SoftAskVote create(SoftAskVoteDto dto, IsAMember member, SoftAsk softAsk, SoftAskReply reply);
  }
}

