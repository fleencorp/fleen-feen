package com.fleencorp.feen.stream.exception;

import com.fleencorp.feen.chat.space.exception.request.AlreadyJoinedChatSpaceException;
import com.fleencorp.feen.chat.space.exception.request.CannotJoinPrivateChatSpaceWithoutApprovalException;
import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.exception.UnableToCompleteOperationException;
import com.fleencorp.feen.stream.exception.attendee.StreamAttendeeNotFoundException;
import com.fleencorp.feen.stream.exception.core.CannotCancelOrDeleteOngoingStreamException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.stream.exception.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.stream.exception.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.stream.exception.speaker.OrganizerOfStreamCannotBeRemovedAsSpeakerException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fleencorp.base.constant.base.ExceptionMessages.invalidRequestBody;
import static com.fleencorp.feen.common.constant.http.FleenHttpStatus.badRequest;
import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.*;

/**
 * A global exception handler for Restful web services.
 *
 * <p>This class is responsible for handling exceptions that occur during the processing of REST API requests.
 * It intercepts exceptions and converts them into appropriate HTTP responses that can be returned to the client.
 * Common exceptions such as validation errors, access violations, and internal server errors are handled
 * and mapped to meaningful response entities with relevant HTTP status codes.</p>
 *
 * <p>By centralizing exception handling, this class helps improve the readability of controller methods
 * and ensures consistent error responses across the entire application.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@RestControllerAdvice
public class StreamExceptionHandler {

  private final ErrorLocalizer localizer;
  private static final String DATA_FIELD_NAME = "field";
  private static final String ERRORS_PROPERTY_NAME = "errors";

  public StreamExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    FailedOperationException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final FailedOperationException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }

  @ExceptionHandler(value = {
    CannotCancelOrDeleteOngoingStreamException.class,
    CannotJoinPrivateChatSpaceWithoutApprovalException.class,
    CannotJoinPrivateStreamWithoutApprovalException.class,
    StreamNotCreatedByUserException.class,
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }

  @ExceptionHandler(value = {
    AlreadyRequestedToJoinStreamException.class,
    AlreadyApprovedRequestToJoinException.class,
    AlreadyJoinedChatSpaceException.class,
    OrganizerOfStreamCannotBeRemovedAsSpeakerException.class,
    StreamAlreadyCanceledException.class,
    StreamAlreadyHappenedException.class,
  })
  @ResponseStatus(value = CONFLICT)
  public ErrorResponse handleConflict(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.conflict());
  }

  @ExceptionHandler(value = {
    UnableToCompleteOperationException.class
  })
  @ResponseStatus(value = INTERNAL_SERVER_ERROR)
  public ErrorResponse handleInternal(final UnableToCompleteOperationException e) {
    return localizer.withStatus(e, FleenHttpStatus.internalServerError());
  }

  @ExceptionHandler(value = {
    StreamAttendeeNotFoundException.class,
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }

}
