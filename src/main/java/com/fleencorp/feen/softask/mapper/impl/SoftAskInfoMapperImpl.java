package com.fleencorp.feen.softask.mapper.impl;

import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.softask.constant.info.count.ReplyCount;
import com.fleencorp.feen.softask.constant.info.count.VoteCount;
import com.fleencorp.feen.softask.constant.info.vote.IsVoted;
import com.fleencorp.feen.softask.constant.info.vote.TotalSoftAskVoted;
import com.fleencorp.feen.softask.mapper.SoftAskInfoMapper;
import com.fleencorp.feen.softask.model.info.SoftAskReplyCountInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskConversationVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskReplyVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskVotedInfo;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public final class SoftAskInfoMapperImpl extends BaseMapper implements SoftAskInfoMapper {

  public SoftAskInfoMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

  /**
   * Maps a boolean vote value to a {@link SoftAskUserVoteInfo} object, including a localized message
   * indicating whether the user has voted.
   *
   * @param voted {@code true} if the user has voted; {@code false} otherwise.
   * @return a {@link SoftAskUserVoteInfo} instance containing the vote status and a localized message.
   */
  @Override
  public SoftAskUserVoteInfo toUserVoteInfo(final boolean voted) {
    final IsVoted isVoted = IsVoted.by(voted);
    return SoftAskUserVoteInfo.of(voted, translate(isVoted.getMessageCode()));
  }

  /**
   * Maps the given total vote count to a {@link SoftAskVoteCountInfo} object, including two
   * localized messages related to the total vote count.
   *
   * @param totalVoteCount the total number of votes received; can be {@code null}.
   * @return a {@link SoftAskVoteCountInfo} instance containing the vote count and two
   *         localized messages based on it.
   */
  @Override
  public SoftAskVoteCountInfo toVoteCountInfo(final Integer totalVoteCount) {
    final VoteCount voteCount = VoteCount.totalVote();
    return SoftAskVoteCountInfo.of(
      totalVoteCount,
      translate(voteCount.getMessageCode(), totalVoteCount),
      translate(voteCount.getMessageCode2(), totalVoteCount)
    );
  }

  /**
   * Maps the given total reply count to a {@link SoftAskReplyCountInfo} object, including a
   * localized message based on the reply count.
   *
   * @param totalReplyCount the total number of replies; can be {@code null}.
   * @return a {@link SoftAskReplyCountInfo} instance containing the reply count and a
   *         localized message derived from it.
   */
  @Override
  public SoftAskReplyCountInfo toReplyCountInfo(final Integer totalReplyCount) {
    final ReplyCount replyCount = ReplyCount.totalReply();
    return SoftAskReplyCountInfo.of(
      totalReplyCount,
      translate(replyCount.getMessageCode(), totalReplyCount)
    );
  }

  /**
   * Converts the given total vote count for SoftAsk replies into a {@link TotalSoftAskReplyVotedInfo}
   * object, including a localized message indicating the total vote status.
   *
   * @param totalVoteCount the total number of votes cast on SoftAsk replies; can be {@code null}.
   * @return a {@link TotalSoftAskReplyVotedInfo} instance containing the vote count and a
   *         localized message.
   */
  @Override
  public TotalSoftAskReplyVotedInfo toTotalSoftAskReplyVotedInfo(final Integer totalVoteCount) {
    final TotalSoftAskVoted totalSoftAskVoted = TotalSoftAskVoted.totalSoftAskReplyVoted();
    return TotalSoftAskReplyVotedInfo.of(totalVoteCount, translate(totalSoftAskVoted.getMessageCode()));
  }

  /**
   * Converts the given total vote count for all SoftAsk entries into a {@link TotalSoftAskVotedInfo}
   * object, including a localized message indicating the overall vote status.
   *
   * @param totalVoteCount the total number of votes cast across all SoftAsk entries; can be {@code null}.
   * @return a {@link TotalSoftAskVotedInfo} instance containing the vote count and a
   *         localized message.
   */
  @Override
  public TotalSoftAskVotedInfo toTotalSoftAskVotedInfo(final Integer totalVoteCount) {
    final TotalSoftAskVoted totalSoftAskVoted = TotalSoftAskVoted.totalSoftAskVoted();
    return TotalSoftAskVotedInfo.of(totalVoteCount, translate(totalSoftAskVoted.getMessageCode()));
  }

  /**
   * Converts the given total vote count for SoftAsk conversations into a {@link TotalSoftAskConversationVotedInfo}
   * object, including a localized message indicating the total conversation vote status.
   *
   * @param totalVoteCount the total number of votes cast on SoftAsk conversations; can be {@code null}.
   * @return a {@link TotalSoftAskConversationVotedInfo} instance containing the vote count and a
   *         localized message.
   */
  @Override
  public TotalSoftAskConversationVotedInfo toTotalSoftAskConversationVotedInfo(final Integer totalVoteCount) {
    final TotalSoftAskVoted totalSoftAskVoted = TotalSoftAskVoted.getTotalSoftAskConversationVoted();
    return TotalSoftAskConversationVotedInfo.of(totalVoteCount, translate(totalSoftAskVoted.getMessageCode()));
  }
}
