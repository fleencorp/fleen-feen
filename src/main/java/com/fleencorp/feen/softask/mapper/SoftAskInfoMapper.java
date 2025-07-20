package com.fleencorp.feen.softask.mapper;

import com.fleencorp.feen.softask.model.info.SoftAskAnswerCountInfo;
import com.fleencorp.feen.softask.model.info.SoftAskReplyCountInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskAnswerVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskConversationVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskReplyVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskVotedInfo;

public interface SoftAskInfoMapper {

  SoftAskUserVoteInfo toUserVoteInfo(boolean voted);

  SoftAskVoteCountInfo toVoteCountInfo(Integer totalVoteCount);

  SoftAskReplyCountInfo toReplyCountInfo(Integer totalReplyCount);

  SoftAskAnswerCountInfo toAnswerCountInfo(Integer totalAnswerCount);

  TotalSoftAskVotedInfo toTotalSoftAskVotedInfo(Integer totalVoteCount);

  TotalSoftAskAnswerVotedInfo toTotalSoftAskAnswerVotedInfo(Integer totalVoteCount);

  TotalSoftAskReplyVotedInfo toTotalSoftAskReplyVotedInfo(Integer totalVoteCount);

  TotalSoftAskConversationVotedInfo toTotalSoftAskConversationVotedInfo(Integer totalVoteCount);
}
