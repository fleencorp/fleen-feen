package com.fleencorp.feen.softask.constant.core.vote;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum SoftAskVoteType implements ApiParameter {

  NOT_VOTED("Not Voted"),
  VOTED("Voted");

  private final String value;

  SoftAskVoteType(final String value) {
    this.value = value;
  }

  public static SoftAskVoteType of(final String value) {
    return parseEnumOrNull(value, SoftAskVoteType.class);
  }

  public static boolean isVoted(final SoftAskVoteType softAskVoteType) {
    return VOTED == softAskVoteType;
  }

  public static boolean voted(final SoftAskVoteType softAskVoteType) {
    return VOTED == softAskVoteType;
  }
}
