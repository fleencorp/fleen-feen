package com.fleencorp.feen.poll.exception;

import com.fleencorp.feen.constant.http.FleenHttpStatus;
import com.fleencorp.feen.poll.exception.option.PollOptionNotFoundException;
import com.fleencorp.feen.poll.exception.option.PollUpdateCantChangeOptionsException;
import com.fleencorp.feen.poll.exception.poll.*;
import com.fleencorp.feen.poll.exception.vote.PollVotingNoMultipleChoiceException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollDeletedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollEndedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollNoOptionException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.poll"})
public class PollExceptionHandler {

  private final ErrorLocalizer localizer;

  public PollExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    PollUpdateCantChangeOptionsException.class,
    PollUpdateCantChangeAnonymityException.class,
    PollUpdateCantChangeMultipleChoiceException.class,
    PollUpdateCantChangeQuestionException.class,
    PollUpdateCantChangeVisibilityException.class,
    PollUpdateUnauthorizedException.class,
    PollVotingNoMultipleChoiceException.class,
    PollVotingNotAllowedPollDeletedException.class,
    PollVotingNotAllowedPollDeletedException.class,
    PollVotingNotAllowedPollEndedException.class,
    PollVotingNotAllowedPollNoOptionException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }

  @ExceptionHandler(value = {
    PollOptionNotFoundException.class,
    PollNotFoundException.class
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }
}
