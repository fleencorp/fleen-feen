package com.fleencorp.feen.softask.constant.info.count;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum ReplyCount implements ApiParameter {

  TOTAL_REPLY("Total Reply", "soft.ask.reply.total.count");

  private final String value;
  private final String messageCode;

  ReplyCount(final String value, final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static ReplyCount totalReply() {
    return TOTAL_REPLY;
  }
}
