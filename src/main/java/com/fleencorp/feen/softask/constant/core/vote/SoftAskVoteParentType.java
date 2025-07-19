package com.fleencorp.feen.softask.constant.core.vote;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum SoftAskVoteParentType implements ApiParameter {

  SOFT_ASK_ANSWER("Soft Ask Answer"),
  SOFT_ASK_REPLY("Soft Ask Reply"),
  SOFT_ASK("Soft Ask");

  private final String value;

  SoftAskVoteParentType(final String value) {
    this.value = value;
  }

  public static SoftAskVoteParentType of(final String value) {
    return parseEnumOrNull(value, SoftAskVoteParentType.class);
  }

  public static boolean isSoftAsk(final SoftAskVoteParentType softAskVoteParentType) {
    return SOFT_ASK == softAskVoteParentType;
  }

  public static boolean isSoftAskAnswer(final SoftAskVoteParentType softAskVoteParentType) {
    return SOFT_ASK_ANSWER == softAskVoteParentType;
  }

  public static boolean isSoftAskReply(final SoftAskVoteParentType softAskVoteParentType) {
    return SOFT_ASK_REPLY == softAskVoteParentType;
  }
}
