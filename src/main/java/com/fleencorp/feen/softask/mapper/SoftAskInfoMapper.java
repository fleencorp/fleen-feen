package com.fleencorp.feen.softask.mapper;

import com.fleencorp.feen.softask.model.info.reply.SoftAskReplyCountInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskConversationVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskReplyVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskVotedInfo;

public interface SoftAskInfoMapper {

  SoftAskUserVoteInfo toUserVoteInfo(boolean voted);

  SoftAskVoteCountInfo toVoteCountInfo(Integer totalVoteCount);

  SoftAskReplyCountInfo toReplyCountInfo(Integer totalReplyCount);

  TotalSoftAskVotedInfo toTotalSoftAskVotedInfo(Integer totalVoteCount);

  TotalSoftAskReplyVotedInfo toTotalSoftAskReplyVotedInfo(Integer totalVoteCount);

  TotalSoftAskConversationVotedInfo toTotalSoftAskConversationVotedInfo(Integer totalVoteCount);
}
