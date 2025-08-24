package com.fleencorp.feen.softask.constant.info.vote;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum TotalSoftAskVoted implements ApiParameter {

  TOTAL_SOFT_ASK_REPLY_VOTED("total.soft.ask.reply.voted"),
  TOTAL_SOFT_ASK_VOTED("total.soft.ask.voted"),
  TOTAL_SOFT_ASK_CONVERSATION_VOTED("total.soft.ask.conversation.voted"),;

  private final String value;

  TotalSoftAskVoted(final String value) {
    this.value = value;
  }

  public String getMessageCode() {
    return value;
  }

  public static TotalSoftAskVoted totalSoftAskReplyVoted() {
    return TOTAL_SOFT_ASK_REPLY_VOTED;
  }

  public static TotalSoftAskVoted totalSoftAskVoted() {
    return TOTAL_SOFT_ASK_VOTED;
  }

  public static TotalSoftAskVoted getTotalSoftAskConversationVoted() {
    return TOTAL_SOFT_ASK_CONVERSATION_VOTED;
  }
}
