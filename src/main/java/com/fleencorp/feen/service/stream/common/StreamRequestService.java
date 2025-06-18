package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.stream.base.RescheduleStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamDto;
import com.fleencorp.feen.model.request.stream.ExternalStreamRequest;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;

public interface StreamRequestService {

  /**
   * Creates a {@link ExternalStreamRequest} using the provided details to update a stream's properties.
   *
   * <p>This method constructs a {@link ExternalStreamRequest} object based on the provided stream, calendar,
   * and update details (such as title, description, and location) from the {@link UpdateStreamDto} and OAuth2
   * authorization details.</p>
   *
   * @param calendar the {@link Calendar} object associated with the stream
   * @param oauth2Authorization the {@link Oauth2Authorization} used for authentication during the patch request
   * @param stream the {@link FleenStream} object containing the stream's details
   * @param updateStreamDto the {@link UpdateStreamDto} containing updated stream properties such as title, description, and location
   * @return a {@link ExternalStreamRequest} object containing the updated stream details
   */
  default ExternalStreamRequest createPatchStreamRequest(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final UpdateStreamDto updateStreamDto) {
    return ExternalStreamRequest.ofPatch(
      calendar,
      oauth2Authorization,
      stream,
      updateStreamDto.getTitle(),
      updateStreamDto.getDescription(),
      updateStreamDto.getLocation(),
      stream.getStreamType()
    );
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
  default ExternalStreamRequest createRescheduleStreamRequest(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final RescheduleStreamDto rescheduleStreamDto) {
    return ExternalStreamRequest.ofReschedule(
      calendar,
      oauth2Authorization,
      stream,
      rescheduleStreamDto.getTimezone(),
      rescheduleStreamDto.getStartDateTime(),
      rescheduleStreamDto.getStartDateTime(),
      stream.getStreamType()
    );
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
  default ExternalStreamRequest createDeleteStreamRequest(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream) {
    return ExternalStreamRequest.ofDelete(
      calendar,
      oauth2Authorization,
      stream,
      stream.getStreamType()
    );
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
  default ExternalStreamRequest createUpdateStreamVisibilityRequest(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final String visibility) {
    return ExternalStreamRequest.ofVisibilityUpdate(
      calendar,
      oauth2Authorization,
      stream,
      visibility,
      stream.getStreamType()
    );
  }
}
