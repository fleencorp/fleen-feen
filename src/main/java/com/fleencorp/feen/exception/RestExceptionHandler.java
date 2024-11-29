package com.fleencorp.feen.exception;

import com.fleencorp.base.exception.FleenException;
import com.fleencorp.base.model.response.error.ErrorResponse;
import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.constant.http.FleenHttpStatus;
import com.fleencorp.feen.exception.auth.AlreadySignedUpException;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationTokenException;
import com.fleencorp.feen.exception.auth.UsernameNotFoundException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.base.UnableToCompleteOperationException;
import com.fleencorp.feen.exception.calendar.CalendarAlreadyActiveException;
import com.fleencorp.feen.exception.calendar.CalendarAlreadyExistException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.chat.space.*;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.exception.common.CountryNotFoundException;
import com.fleencorp.feen.exception.common.ObjectNotFoundException;
import com.fleencorp.feen.exception.file.FileUploadException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidGrantOrTokenException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidScopeException;
import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.exception.notification.NotificationNotFoundException;
import com.fleencorp.feen.exception.security.mfa.MfaGenerationFailedException;
import com.fleencorp.feen.exception.security.mfa.MfaVerificationFailed;
import com.fleencorp.feen.exception.security.recaptcha.InvalidReCaptchaException;
import com.fleencorp.feen.exception.social.*;
import com.fleencorp.feen.exception.social.contact.ContactNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.exception.user.profile.*;
import com.fleencorp.feen.exception.user.role.NoRoleAvailableToAssignException;
import com.fleencorp.feen.exception.verification.*;
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
import static com.fleencorp.feen.constant.http.FleenHttpStatus.badRequest;
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
public class RestExceptionHandler {

  private final LocalizedResponse localizedResponse;
  private static final String DATA_FIELD_NAME = "field";
  private static final String ERRORS_PROPERTY_NAME = "errors";

  public RestExceptionHandler(final LocalizedResponse localizedResponse) {
    this.localizedResponse = localizedResponse;
  }

  /**
   * Handles exceptions of type {@link FailedOperationException} and returns a {@link ErrorResponse} with a {@code BAD_REQUEST} status.
   *
   * <p>This method is triggered whenever a {@code FailedOperationException} is thrown during the processing of a request.
   * It creates an appropriate error response by delegating to the {@code localizedResponse} service,
   * which formats the response based on the exception details and the localized message.</p>
   *
   * <p>The method sets the HTTP response status to {@code BAD_REQUEST} (400), indicating that the client's request was invalid or could not be processed.</p>
   *
   * @param e the exception thrown during the operation that resulted in a failure
   * @return an {@code ErrorResponse} containing error details and the corresponding {@code BAD_REQUEST} status
   */
  @ExceptionHandler(value = {
    FailedOperationException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final FailedOperationException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.badRequest());
  }

  /**
   * Handles various {@link FleenException} types and returns an {@link ErrorResponse} with a {@code BAD_REQUEST} status.
   *
   * <p>This method is triggered whenever any of the specified exceptions are thrown during the processing of a request.
   * It catches a wide range of application-specific exceptions such as {@link AlreadySignedUpException},
   * {@link BannedAccountException}, {@link InvalidVerificationCodeException}, and many more, all of which indicate a problem
   * with the client's request that prevents it from being processed correctly.</p>
   *
   * <p>The method creates an appropriate error response using the {@code localizedResponse} service,
   * which formats the response based on the exception details and the localized message.
   * The response status is set to {@code BAD_REQUEST} (400), indicating that the request was invalid or could not be processed.</p>
   *
   * @param e the exception thrown during the operation, which could be any one of the listed exceptions
   * @return an {@code ErrorResponse} containing error details and the corresponding {@code BAD_REQUEST} status
   */
  @ExceptionHandler(value = {
    AlreadySignedUpException.class,
    BannedAccountException.class,
    CannotCancelShareContactRequestException.class,
    CannotCancelOrDeleteOngoingStreamException.class,
    CannotJoinPrivateChatSpaceException.class,
    CannotJointStreamWithoutApprovalException.class,
    CannotProcessShareContactRequestException.class,
    ChatSpaceNotActiveException.class,
    DisabledAccountException.class,
    ExpiredVerificationCodeException.class,
    FileUploadException.class,
    FleenStreamNotCreatedByUserException.class,
    InvalidVerificationCodeException.class,
    MfaGenerationFailedException.class,
    MfaVerificationFailed.class,
    NoRoleAvailableToAssignException.class,
    NotAnAdminOfChatSpaceException.class,
    RequestToJoinChatSpacePendingException.class,
    ResetPasswordCodeExpiredException.class,
    ResetPasswordCodeInvalidException.class,
    ShareContactRequestValueRequiredException.class,
    UpdatePasswordFailedException.class,
    UpdateProfileInfoFailedException.class,
    VerificationFailedException.class,
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final FleenException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.badRequest());
  }

  /**
   * Handles external authorization and reCAPTCHA related exceptions, returning an appropriate error response with a {@code BAD_REQUEST} status.
   *
   * <p>This method is triggered when specific external-related exceptions such as {@link InvalidReCaptchaException},
   * {@link Oauth2InvalidAuthorizationException}, {@link Oauth2InvalidGrantOrTokenException}, or {@link Oauth2InvalidScopeException}
   * are thrown during request processing. These exceptions typically indicate issues with third-party services like reCAPTCHA or OAuth2.</p>
   *
   * <p>The method uses the {@code localizedResponse} service to generate a localized error response,
   * though the status in the response is mapped to {@code NOT_FOUND} (404), despite the original request error leading to a {@code BAD_REQUEST}.
   * This custom handling may be intended to mask certain error details.</p>
   *
   * @param e the exception thrown, representing an external error such as invalid reCAPTCHA or OAuth2 authorization issues
   * @return a localized error response with a {@code NOT_FOUND} status
   */
  @ExceptionHandler(value = {
    InvalidReCaptchaException.class,
    Oauth2InvalidAuthorizationException.class,
    Oauth2InvalidGrantOrTokenException.class,
    Oauth2InvalidScopeException.class,
  })
  @ResponseStatus(value = BAD_REQUEST)
  public Object handleExternalBadRequest(final FleenException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.badRequest());
  }

  /**
   * Handles various exceptions that indicate a conflict in the application's state, returning an {@link ErrorResponse} with a {@code CONFLICT} status.
   *
   * <p>This method is triggered when specific exceptions, such as {@link AlreadyRequestedToJoinStreamException},
   * {@link AlreadyJoinedChatSpaceException}, {@link CalendarAlreadyActiveException}, and others, occur during the processing of a request.
   * These exceptions typically indicate that an operation cannot be completed due to a conflict with the current state of the application,
   * such as trying to join a stream that the user has already requested or that a calendar event already exists.</p>
   *
   * <p>The method utilizes the {@code localizedResponse} service to create a localized error response.
   * The HTTP response status is set to {@code CONFLICT} (409), indicating that the request could not be completed due to a conflict with the current resource state.</p>
   *
   * @param e the exception thrown during the operation that represents a conflict in the application's state
   * @return an {@code ErrorResponse} containing details about the conflict and the corresponding {@code CONFLICT} status
   */
  @ExceptionHandler(value = {
    AlreadyRequestedToJoinStreamException.class,
    AlreadyApprovedRequestToJoinException.class,
    AlreadyJoinedChatSpaceException.class,
    CalendarAlreadyActiveException.class,
    CalendarAlreadyExistException.class,
    ChatSpaceAlreadyDeletedException.class,
    EmailAddressAlreadyExistsException.class,
    PhoneNumberAlreadyExistsException.class,
    ShareContactRequestAlreadyCanceledException.class,
    ShareContactRequestAlreadyProcessedException.class,
    StreamAlreadyCanceledException.class,
    StreamAlreadyHappenedException.class
  })
  @ResponseStatus(value = CONFLICT)
  public ErrorResponse handleConflict(final FleenException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.conflict());
  }

  /**
   * Handles exceptions indicating that an operation could not be completed due to internal server errors,
   * returning an {@link ErrorResponse} with an {@code INTERNAL_SERVER_ERROR} status.
   *
   * <p>This method is triggered when the {@link UnableToCompleteOperationException} occurs during the processing
   * of a request. This exception typically signifies that an unexpected error has occurred within the server,
   * preventing the completion of the requested operation.</p>
   *
   * <p>The method utilizes the {@code localizedResponse} service to create a localized error response,
   * ensuring the response is relevant to the user's context. The HTTP response status is set to
   * {@code INTERNAL_SERVER_ERROR} (500), indicating that there was a problem on the server side.</p>
   *
   * @param e the exception thrown during the operation indicating an internal server error
   * @return an {@code ErrorResponse} containing details about the internal error and the corresponding
   *         {@code INTERNAL_SERVER_ERROR} status
   */
  @ExceptionHandler(value = {
    UnableToCompleteOperationException.class
  })
  @ResponseStatus(value = INTERNAL_SERVER_ERROR)
  public ErrorResponse handleInternal(final UnableToCompleteOperationException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.internalServerError());
  }

  /**
   * Handles exceptions related to resource not found scenarios, returning an appropriate error response with a {@code NOT_FOUND} status.
   *
   * <p>This method is triggered when specific exceptions, such as {@link CalendarNotFoundException},
   * {@link ChatSpaceNotFoundException}, {@link MemberNotFoundException}, and others, occur during request processing.
   * These exceptions indicate that the requested resource does not exist in the system, such as when attempting to access a chat space or member
   * that cannot be found.</p>
   *
   * <p>The method leverages the {@code localizedResponse} service to generate a localized error response,
   * ensuring that the user receives a meaningful message that corresponds to the {@code NOT_FOUND} (404) HTTP status.</p>
   *
   * @param e the exception thrown during the operation that indicates a resource was not found
   * @return a localized error response with a {@code NOT_FOUND} status, providing details about the missing resource
   */
  @ExceptionHandler(value = {
    CalendarNotFoundException.class,
    ChatSpaceNotFoundException.class,
    ChatSpaceMemberNotFoundException.class,
    ContactNotFoundException.class,
    CountryNotFoundException.class,
    FleenStreamNotFoundException.class,
    MemberNotFoundException.class,
    NotificationNotFoundException.class,
    ObjectNotFoundException.class,
    ShareContactRequestNotFoundException.class,
    UserNotFoundException.class,
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final FleenException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.notFound());
  }

  /**
   * Handles unauthorized access exceptions and provides a localized response.
   *
   * <p>This method intercepts exceptions of types {@link InvalidAuthenticationException} and
   * {@link InvalidAuthenticationTokenException} and responds with a 404 Not Found status.
   * It returns a localized response containing details about the exception and the unauthorized status.</p>
   *
   * @param e the {@link FleenException} being handled.
   * @return a localized response with the unauthorized status.
   */
  @ExceptionHandler(value = {
    InvalidAuthenticationException.class,
    InvalidAuthenticationTokenException.class,
    UsernameNotFoundException.class
  })
  @ResponseStatus(value = UNAUTHORIZED)
  public Object handleUnauthorized(final FleenException e) {
    return localizedResponse.withStatus(e, FleenHttpStatus.unauthorized());
  }

  /**
   * Handles {@link MethodArgumentNotValidException} exceptions and constructs an error response for validation errors.
   *
   * <p>This method is invoked when a {@link MethodArgumentNotValidException} is thrown, typically due to validation
   * errors on the request body. It processes the field-specific errors, converts field names to snake_case, collects
   * validation error messages, and creates an {@link ErrorResponse} containing these details.</p>
   *
   * @param ex  the {@link MethodArgumentNotValidException} that contains validation errors.
   * @return an {@link ErrorResponse} object containing details of the validation errors, including field names and
   *         associated error messages.
   */
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Object handleDataValidationError(final MethodArgumentNotValidException ex) {
    // List to store field-specific validation errors
    final List<Map<String, Object>> fieldErrors = new ArrayList<>();

    // Extracting field errors from the binding result
    ex.getBindingResult()
      .getFieldErrors()
      .forEach(fieldError -> {
        final Map<String, Object> fieldErrorDetails = new HashMap<>();

        // Extracting field name and converting it to snake_case
        final String fieldName = toSnakeCase(fieldError.getField());
        // Extracting validation error messages
        final List<String> errors = getErrStrings(ex, fieldError);

        // Adding field-specific details to the list
        setFieldErrorDetails(fieldName, fieldErrorDetails, errors);
        fieldErrors.add(fieldErrorDetails);
    });

    return ErrorResponse.of(invalidRequestBody(), badRequest(), fieldErrors);
  }

  /**
   * Sets the details of field errors in the provided map.
   *
   * <p>This method populates the given map with field error details, including the field name and a list of error messages.
   * It adds the field name under the key {@code DATA_FIELD_NAME} and the list of error messages under the key {@code ERRORS_PROPERTY_NAME}.</p>
   *
   * @param fieldName        the name of the field associated with the errors.
   * @param fieldErrorDetails the map to which the field error details will be added.
   * @param errors           the list of error messages related to the field.
   */
  protected void setFieldErrorDetails(final String fieldName, final Map<String, Object> fieldErrorDetails, final List<String> errors) {
    fieldErrorDetails.put(DATA_FIELD_NAME, fieldName);
    fieldErrorDetails.put(ERRORS_PROPERTY_NAME, errors);
  }

  /**
   * Retrieves a list of error messages for a specific field from the provided exception.
   *
   * <p>This method extracts error messages associated with a particular field from a
   * {@link MethodArgumentNotValidException}. It collects the default messages for the given
   * field errors into a list and returns it.</p>
   *
   * @param ex         the {@link MethodArgumentNotValidException} containing the binding result.
   * @param fieldError the {@link FieldError} specifying the field for which error messages are to be retrieved.
   * @return a list of error messages associated with the specified field, or {@code null} if the exception is {@code null}.
   */
  protected static List<String> getErrStrings(final MethodArgumentNotValidException ex, final FieldError fieldError) {
    if (nonNull(ex)) {
      return ex.getBindingResult().getFieldErrors(fieldError.getField())
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList();
    }
    return new ArrayList<>();
  }

  /**
   * Converts the given string from lower camel case to snake case.
   *
   * <p>This method converts the input string from lower camel case format
   * (e.g., "exampleString") to snake case format (e.g., "example_string").</p>
   *
   * @param input  the input string in lower camel case format.
   * @return the converted string in snake case format.
   */
  public static String toSnakeCase(final String input) {
    if (nonNull(input)) {
      return LOWER_CAMEL.to(LOWER_UNDERSCORE, input);
    }
    return null;
  }

}
