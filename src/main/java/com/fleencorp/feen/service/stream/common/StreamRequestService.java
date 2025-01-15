package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.stream.base.RescheduleStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamDto;
import com.fleencorp.feen.model.request.stream.ExternalStreamRequest;

public interface StreamRequestService {

  /**
   * Creates a {@link ExternalStreamRequest} using the provided details to update a stream's properties.
   *
   * <p>This method constructs a {@link ExternalStreamRequest} object based on the provided stream and update
   * details (such as title, description, and location) from the {@link UpdateStreamDto}. The OAuth2 authorization
   * is left as {@code null} in this case, assuming that it might not be needed for this particular patch operation.</p>
   *
   * @param calendar the {@link Calendar} object used to calculate or include time-related details for the patch request
   * @param stream the {@link FleenStream} object containing the stream's current details
   * @param updateStreamDto the {@link UpdateStreamDto} containing updated stream properties such as title, description, and location
   * @return a {@link ExternalStreamRequest} object containing the updated stream details
   */
  default ExternalStreamRequest createPatchStreamRequest(final Calendar calendar, final FleenStream stream, final UpdateStreamDto updateStreamDto) {
    return createPatchStreamRequest(calendar, stream, updateStreamDto, null);
  }

  /**
   * Creates a {@link ExternalStreamRequest} using the provided details to update a stream's properties,
   * without requiring a {@link Calendar} object.
   *
   * <p>This method constructs a {@link ExternalStreamRequest} object based on the provided stream and update
   * details (such as title, description, and location) from the {@link UpdateStreamDto} and OAuth2
   * authorization details. It delegates the creation to the other overloaded method, passing {@code null}
   * for the calendar parameter.</p>
   *
   * @param oauth2Authorization the {@link Oauth2Authorization} used for authentication during the patch request
   * @param stream the {@link FleenStream} object containing the stream's details
   * @param updateStreamDto the {@link UpdateStreamDto} containing updated stream properties such as title, description, and location
   * @return a {@link ExternalStreamRequest} object containing the updated stream details
   */
  default ExternalStreamRequest createPatchStreamRequest(final Oauth2Authorization oauth2Authorization, final FleenStream stream, final UpdateStreamDto updateStreamDto) {
    return createPatchStreamRequest(null, stream, updateStreamDto, oauth2Authorization);
  }

  /**
   * Creates a {@link ExternalStreamRequest} using the provided details to update a stream's properties.
   *
   * <p>This method constructs a {@link ExternalStreamRequest} object based on the provided stream, calendar,
   * and update details (such as title, description, and location) from the {@link UpdateStreamDto} and OAuth2
   * authorization details.</p>
   *
   * @param calendar the {@link Calendar} object associated with the stream
   * @param stream the {@link FleenStream} object containing the stream's details
   * @param updateStreamDto the {@link UpdateStreamDto} containing updated stream properties such as title, description, and location
   * @param oauth2Authorization the {@link Oauth2Authorization} used for authentication during the patch request
   * @return a {@link ExternalStreamRequest} object containing the updated stream details
   */
  default ExternalStreamRequest createPatchStreamRequest(final Calendar calendar, final FleenStream stream, final UpdateStreamDto updateStreamDto, final Oauth2Authorization oauth2Authorization) {
    return ExternalStreamRequest.ofPatch(
      calendar,
      stream,
      updateStreamDto.getTitle(),
      updateStreamDto.getDescription(),
      updateStreamDto.getLocation(),
      oauth2Authorization,
      stream.getStreamType()
    );
  }

  /**
   * Creates a {@link ExternalStreamRequest} to reschedule a stream based on the provided details.
   *
   * <p>This method constructs a {@link ExternalStreamRequest} object using the provided {@link Calendar},
   * {@link FleenStream}, and {@link RescheduleStreamDto}. The {@link Calendar} helps include time-related
   * information in the request, while the {@link RescheduleStreamDto} contains the new scheduling details such
   * as the new start time, end time, and timezone. The OAuth2 authorization is set to {@code null} in this case.</p>
   *
   * @param calendar the {@link Calendar} object used to include time-related details for the reschedule request
   * @param stream the {@link FleenStream} object that represents the stream being rescheduled
   * @param rescheduleStreamDto the {@link RescheduleStreamDto} containing the new schedule details
   * @return a {@link ExternalStreamRequest} object with the updated scheduling information
   */
  default ExternalStreamRequest createRescheduleStreamRequest(final Calendar calendar, final FleenStream stream, final RescheduleStreamDto rescheduleStreamDto) {
    return createRescheduleStreamRequest(calendar, stream, rescheduleStreamDto, null);
  }

  /**
   * Creates a {@link ExternalStreamRequest} to reschedule a stream with OAuth2 authorization details.
   *
   * <p>This method constructs a {@link ExternalStreamRequest} object using the provided {@link Oauth2Authorization},
   * {@link FleenStream}, and {@link RescheduleStreamDto}. The {@link Oauth2Authorization} is used for authentication
   * while making the request, ensuring that the request is authorized to perform actions on the stream.</p>
   *
   * @param oauth2Authorization the {@link Oauth2Authorization} used to authenticate the request
   * @param stream the {@link FleenStream} object representing the stream to be rescheduled
   * @param rescheduleStreamDto the {@link RescheduleStreamDto} containing the new scheduling details
   * @return a {@link ExternalStreamRequest} object with the updated scheduling information
   */
  default ExternalStreamRequest createRescheduleStreamRequest(final Oauth2Authorization oauth2Authorization, final FleenStream stream, final RescheduleStreamDto rescheduleStreamDto) {
    return createRescheduleStreamRequest(null, stream, rescheduleStreamDto, oauth2Authorization);
  }

  /**
   * Creates a request to reschedule a stream, using the provided calendar, stream, and reschedule details.
   *
   * <p>This method builds a {@link ExternalStreamRequest} based on the stream to be rescheduled, including
   * details such as the calendar, the stream, the new timezone, and the updated start date and time for the stream.
   * It uses the {@link RescheduleStreamDto} to extract the necessary information and constructs the request.</p>
   *
   * @param calendar the calendar associated with the stream that needs to be rescheduled
   * @param stream the {@link FleenStream} object representing the stream to be rescheduled
   * @param rescheduleStreamDto the DTO containing the new reschedule details (timezone and start time)
   * @return a {@link ExternalStreamRequest} containing all the information needed to reschedule the stream
   */
  default ExternalStreamRequest createRescheduleStreamRequest(final Calendar calendar, final FleenStream stream, final RescheduleStreamDto rescheduleStreamDto, final Oauth2Authorization oauth2Authorization) {
    return ExternalStreamRequest.ofReschedule(
      calendar,
      stream,
      rescheduleStreamDto.getTimezone(),
      rescheduleStreamDto.getStartDateTime(),
      rescheduleStreamDto.getStartDateTime(),
      oauth2Authorization,
      stream.getStreamType()
    );
  }

  /**
   * Creates a request to delete a stream, with calendar information but without an OAuth2 authorization token.
   *
   * <p>This method prepares a {@link ExternalStreamRequest} for deleting a stream using the provided {@link FleenStream}
   * entity and {@link Calendar} details. The OAuth2 authorization token is omitted in this case.</p>
   *
   * @param stream the {@link FleenStream} entity representing the stream to be deleted
   * @param calendar the {@link Calendar} representing the schedule of the stream (may be used for other purposes)
   *
   * @return a {@link ExternalStreamRequest} containing the necessary details to delete the stream
   */
  default ExternalStreamRequest createDeleteStreamRequest(final FleenStream stream, final Calendar calendar) {
    return createDeleteStreamRequest(stream, calendar, null);
  }

  /**
   * Creates a request to delete a stream, without requiring calendar information.
   *
   * <p>This method prepares a {@link ExternalStreamRequest} for deleting a stream using the provided {@link FleenStream}
   * entity and {@link Oauth2Authorization}. The calendar information is omitted in this case.</p>
   *
   * @param stream the {@link FleenStream} entity representing the stream to be deleted
   * @param oauth2Authorization the {@link Oauth2Authorization} containing the OAuth2 token needed for authorization
   *
   * @return a {@link ExternalStreamRequest} containing the necessary details to delete the stream
   */
  default ExternalStreamRequest createDeleteStreamRequest(final FleenStream stream, final Oauth2Authorization oauth2Authorization) {
    return createDeleteStreamRequest(stream, null, oauth2Authorization);
  }

  /**
   * Creates a request to delete a stream.
   *
   * <p>This method prepares a {@link ExternalStreamRequest} for deleting a stream, using the provided {@link FleenStream}
   * entity, calendar information, and OAuth2 authorization details.</p>
   *
   * @param stream the {@link FleenStream} entity representing the stream to be deleted
   * @param calendar the {@link Calendar} object containing the schedule information for the stream
   * @param oauth2Authorization the {@link Oauth2Authorization} containing the OAuth2 token needed for authorization
   *
   * @return a {@link ExternalStreamRequest} containing the necessary details to delete the stream
   */
  default ExternalStreamRequest createDeleteStreamRequest(final FleenStream stream, final Calendar calendar, final Oauth2Authorization oauth2Authorization) {
    return ExternalStreamRequest.ofDelete(
      calendar,
      stream,
      oauth2Authorization,
      stream.getStreamType()
    );
  }

  /**
   * Creates a new {@link ExternalStreamRequest} using the provided parameters,
   * defaulting the {@link Oauth2Authorization} to {@code null}.
   *
   * @param calendar The calendar instance representing the time context for the request.
   * @param stream The {@link FleenStream} object whose visibility is being updated.
   * @param visibility The desired visibility value for the stream.
   * @return A new {@link ExternalStreamRequest} object initialized with the provided parameters
   *         and a {@code null} Oauth2Authorization.
   */
  default ExternalStreamRequest createUpdateStreamVisibilityRequest(final Calendar calendar, final FleenStream stream, final String visibility) {
    return createUpdateStreamVisibilityRequest(calendar, stream, null, visibility);
  }

  /**
   * Creates a new {@link ExternalStreamRequest} using the provided parameters,
   * defaulting the calendar to {@code null}.
   *
   * @param oauth2Authorization The {@link Oauth2Authorization} object containing the authorization details for the request.
   * @param stream The {@link FleenStream} object whose visibility is being updated.
   * @param visibility The desired visibility value for the stream.
   * @return A new {@link ExternalStreamRequest} object initialized with the provided parameters and a {@code null} calendar.
   */
  default ExternalStreamRequest createUpdateStreamVisibilityRequest(final Oauth2Authorization oauth2Authorization, final FleenStream stream, final String visibility) {
    return createUpdateStreamVisibilityRequest(null, stream, oauth2Authorization, visibility);
  }

  /**
   * Creates a new {@link ExternalStreamRequest} using the provided parameters.
   *
   * @param calendar The calendar instance representing the time context for the request.
   * @param stream The {@link FleenStream} object whose visibility is being updated.
   * @param oauth2Authorization The {@link Oauth2Authorization} object containing the authorization details for the request.
   * @param visibility The desired visibility value for the stream.
   * @return A new {@link ExternalStreamRequest} object initialized with the provided parameters.
   */
  default ExternalStreamRequest createUpdateStreamVisibilityRequest(final Calendar calendar, final FleenStream stream, final Oauth2Authorization oauth2Authorization, final String visibility) {
    return ExternalStreamRequest.ofVisibilityUpdate(
      calendar,
      stream,
      oauth2Authorization,
      visibility,
      stream.getStreamType()
    );
  }
}
