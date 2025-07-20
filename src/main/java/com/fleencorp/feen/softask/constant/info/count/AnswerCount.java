package com.fleencorp.feen.softask.constant.info.count;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum AnswerCount implements ApiParameter {

  TOTAL_ANSWER("Total Answer", "soft.ask.answer.total.count", "soft.ask.answer.total.count.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  AnswerCount(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static AnswerCount totalAnswer() {
    return TOTAL_ANSWER;
  }
}
