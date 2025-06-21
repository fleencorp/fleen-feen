package com.fleencorp.feen.poll.model.form;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum PollFormField implements ApiParameter {

  DESCRIPTION("poll.form.model.description.description"),
  EXPIRES_AT("poll.form.model.description.expiresAt"),
  IS_ANONYMOUS("poll.form.model.description.isAnonymous"),
  IS_MULTIPLE_CHOICE("poll.form.model.description.isMultipleChoice"),
  OPTIONS("poll.form.model.description.options"),
  TITLE("poll.form.model.description.title"),
  VISIBILITY("poll.form.model.description.visibility");

  private final String value;

  PollFormField(final String value) {
    this.value = value;
  }

  public String getDescription() {
    return value;
  }
}
