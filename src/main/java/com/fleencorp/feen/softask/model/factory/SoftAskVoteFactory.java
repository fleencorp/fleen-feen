package com.fleencorp.feen.softask.model.factory;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;

import java.util.Map;

public final class SoftAskVoteFactory {

  private SoftAskVoteFactory() {}

  private static final Map<SoftAskVoteParentType, VoteCreator> CREATORS = Map.of(
    SoftAskVoteParentType.SOFT_ASK, SoftAskVoteFactory::createSoftAskVote,
    SoftAskVoteParentType.SOFT_ASK_REPLY, SoftAskVoteFactory::createSoftAskReplyVote
  );

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
    if (dto == null || member == null) {
      throw FailedOperationException.of();
    }
  }

  private static SoftAskVote createSoftAskVote(SoftAskVoteDto dto, IsAMember member, SoftAsk softAsk, SoftAskReply unused) {

    final SoftAskVote vote = baseVote(dto, member, softAsk.getSoftAskId());
    vote.setSoftAskId(softAsk.getSoftAskId());
    vote.setSoftAsk(softAsk);
    vote.setParentTitle(softAsk.getTitle());

    return vote;
  }

  private static SoftAskVote createSoftAskReplyVote(SoftAskVoteDto dto, IsAMember member, SoftAsk softAsk, SoftAskReply reply) {

    final SoftAskVote vote = baseVote(dto, member, reply.getSoftAskReplyId());
    vote.setSoftAskReplyId(reply.getSoftAskReplyId());
    vote.setSoftAskReply(reply);
    vote.setSoftAskId(softAsk.getSoftAskId());
    vote.setSoftAsk(softAsk);
    vote.setParentTitle(reply.getTitle());

    return vote;
  }

  private static SoftAskVote baseVote(SoftAskVoteDto dto, IsAMember member, Long parentId) {
    final SoftAskVote vote = new SoftAskVote();
    vote.setParentId(parentId);
    vote.setParentType(dto.getVoteParentType());
    vote.setVoteType(dto.getVoteType());
    vote.setMemberId(member.getMemberId());

    return vote;
  }

  @FunctionalInterface
  private interface VoteCreator {
    SoftAskVote create(SoftAskVoteDto dto, IsAMember member, SoftAsk softAsk, SoftAskReply reply);
  }
}

