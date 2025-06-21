package com.fleencorp.feen.poll.model.form;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum PollFormField implements ApiParameter {

  TITLE("poll.form.model.description.title"),
  DESCRIPTION("poll.form.model.description.description"),
  OPTIONS("poll.form.model.description.options"),
  IS_MULTIPLE_CHOICE("poll.form.model.description.isMultipleChoice"),
  IS_ANONYMOUS("poll.form.model.description.isAnonymous"),
  EXPIRES_AT("poll.form.model.description.expiresAt");

  private final String value;

  PollFormField(final String value) {
    this.value = value;
  }

  public String getDescription() {
    return value;
  }
}
