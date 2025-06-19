package com.fleencorp.feen.poll.exception.option;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollOptionNotFoundException extends LocalizedException {

  public PollOptionNotFoundException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "poll.option.not.found";
  }

  public static PollOptionNotFoundException of(final Object...optionIds) {
    return new PollOptionNotFoundException(optionIds);
  }
}
