package com.fleencorp.feen.model.request.stream;

import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.constant.external.request.ExternalStreamRequestType;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.event.CreateEventDto;
import com.fleencorp.feen.model.dto.event.CreateInstantEventDto;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fleencorp.feen.model.dto.event.CreateEventDto.EventAttendeeOrGuest;

/**
 * Represents a request for managing external streams such as live broadcasts,
 * events, and calendar-related operations. This class encapsulates the data
 * and behavior necessary to create, update, delete, reschedule, and manage
 * the visibility of external streams, as well as handle event attendance.
 *
 * <p>The class includes various properties such as stream details, event details,
 * authorization tokens, attendee information, and timestamps, and provides
 * factory methods to create instances for different types of stream operations.</p>
 *
 * <p>The types of requests that can be created using this class include:</p>
 * <ul>
 * <li>Event creation</li>
 * <lI>Instant event creation</lI>
 * <li>Live broadcast creation</li>
 * <li>Stream visibility updates</li>
 * <li>Event rescheduling</li>
 * <li>Request cancellation, etc.</li>
 * </ul>
 *
 * <p>Each method within the class helps to identify or handle specific types of requests.
 * For instance, methods like {@code isCreateEventRequest()} and {@code isCancelRequest()}
 * allow checking the nature of the request, while factory methods such as
 * {@code ofCreateEvent()} and {@code ofDelete()} facilitate the creation of
 * request objects.</p>
 *
 * <p>This class supports OAuth 2.0 authorization via the {@link Oauth2Authorization} object
 * and manages attendee information through {@link EventAttendeeOrGuest}.</p>
 *
 * Example usage:
 * <pre>
 *    ExternalStreamRequest request = ExternalStreamRequest.ofCreateEvent(calendar, stream, attendee, email, streamType, eventDto);
 * </pre>
 *
 * @author Yusuf Àlàmu Musa
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalStreamRequest {

  private Calendar calendar;
  private FleenStream stream;
  private StreamAttendee attendee;
  private StreamType streamType;
  private Oauth2Authorization oauth2Authorization;
  private String visibility;
  private String title;
  private String description;
  private String location;
  private String timezone;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private CreateEventDto createEventDto;
  private CreateLiveBroadcastDto createLiveBroadcastDto;
  private CreateInstantEventDto createInstantEventDto;
  private JoinStreamDto joinStreamDto;
  private ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto;
  private String attendeeEmailAddress;
  private EventAttendeeOrGuest attendeeOrGuest;

  private ExternalStreamRequestType requestType;

  public String calendarExternalId() {
    return calendar != null ? calendar.getExternalId() : null;
  }

  public String streamExternalId() {
    return stream != null ? stream.getExternalId() : null;
  }

  public String userEmailAddress() {
    return attendeeEmailAddress;
  }

  public String accessToken() {
    return (oauth2Authorization != null) ? oauth2Authorization.getAccessToken() : null;
  }

  public boolean isAnEvent() {
    return StreamType.isEvent(streamType);
  }

  public boolean isABroadcast() {
    return StreamType.isLiveStream(streamType);
  }

  public boolean isCancelRequest() {
    return ExternalStreamRequestType.isCancelRequest(requestType);
  }

  public boolean isCreateEventRequest() {
    return ExternalStreamRequestType.isCreateEventRequest(requestType);
  }

  public boolean isCreateInstantEventRequest() {
    return ExternalStreamRequestType.isCreateInstantEvent(requestType);
  }

  public boolean isCreateLiveBroadcastRequest() {
    return ExternalStreamRequestType.isCreateLiveBroadcastRequest(requestType);
  }

  public boolean isDeleteRequest() {
    return ExternalStreamRequestType.isDeleteRequest(requestType);
  }

  public boolean isJoinStreamRequest() {
    return ExternalStreamRequestType.isJoinStreamRequest(requestType);
  }

  public boolean isNotAttendingRequest() {
    return ExternalStreamRequestType.isNotAttendingRequest(requestType);
  }

  public boolean isPatchRequest() {
    return ExternalStreamRequestType.isPatchRequest(requestType);
  }

  public boolean isRescheduleRequest() {
    return ExternalStreamRequestType.isRescheduleRequest(requestType);
  }

  public boolean isVisibilityUpdateRequest() {
    return ExternalStreamRequestType.isVisibilityUpdate(requestType);
  }

  public boolean isProcessAttendeeRequest() { return ExternalStreamRequestType.isProcessAttendeeJoinRequest(requestType); }

  public static ExternalStreamRequest ofCancel(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final StreamType streamType) {
    final ExternalStreamRequest request = of(calendar, oauth2Authorization, stream, streamType);
    request.setRequestType(ExternalStreamRequestType.cancel());
    return request;
  }

  public static ExternalStreamRequest ofCreateEvent(final Calendar calendar, final FleenStream stream, final EventAttendeeOrGuest attendeeOrGuest, final String userEmailAddress, final StreamType streamType, final CreateEventDto createEventDto) {
    final ExternalStreamRequest request = of(calendar, stream, streamType);
    request.setAttendeeOrGuest(attendeeOrGuest);
    request.setAttendeeEmailAddress(userEmailAddress);
    request.setCreateEventDto(createEventDto);
    request.setRequestType(ExternalStreamRequestType.createEvent());
    return request;
  }

  public static ExternalStreamRequest ofCreateInstantEvent(final Calendar calendar, final FleenStream stream, final StreamType streamType, final CreateInstantEventDto createInstantEventDto) {
    final ExternalStreamRequest request = of(calendar, stream, streamType);
    request.setCreateInstantEventDto(createInstantEventDto);
    request.setRequestType(ExternalStreamRequestType.createInstantEvent());
    return request;
  }

  public static ExternalStreamRequest ofCreateLiveBroadcast(final Oauth2Authorization oauth2Authorization, final FleenStream stream, final StreamType streamType, final CreateLiveBroadcastDto createLiveBroadcastDto) {
    final ExternalStreamRequest request = of(null, oauth2Authorization, stream, streamType);
    request.setCreateLiveBroadcastDto(createLiveBroadcastDto);
    request.setRequestType(ExternalStreamRequestType.createLiveBroadcast());
    return request;
  }

  public static ExternalStreamRequest ofDelete(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final StreamType streamType) {
    final ExternalStreamRequest request = of(calendar, oauth2Authorization, stream, streamType);
    request.setRequestType(ExternalStreamRequestType.delete());
    return request;
  }

  public static ExternalStreamRequest ofNotAttending(final Calendar calendar, final FleenStream stream, final String attendeeEmailAddress, final StreamType streamType) {
    final ExternalStreamRequest request = of(calendar, stream, streamType);
    request.setAttendeeEmailAddress(attendeeEmailAddress);
    request.setRequestType(ExternalStreamRequestType.notAttending());
    return request;
  }

  public static ExternalStreamRequest ofPatch(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final String title, final String description, final String location, final StreamType streamType) {
    final ExternalStreamRequest request = of(calendar, oauth2Authorization, stream, streamType);
    request.setTitle(title);
    request.setDescription(description);
    request.setLocation(location);
    request.setRequestType(ExternalStreamRequestType.patch());
    return request;
  }

  public static ExternalStreamRequest ofReschedule(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final String timezone, final LocalDateTime startDateTime, final LocalDateTime endDateTime, final StreamType streamType) {
    final ExternalStreamRequest request = of(calendar, oauth2Authorization, stream, streamType);
    request.setTimezone(timezone);
    request.setStartDateTime(startDateTime);
    request.setEndDateTime(endDateTime);
    request.setRequestType(ExternalStreamRequestType.reschedule());
    return request;
  }

  public static ExternalStreamRequest ofVisibilityUpdate(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final String visibility, final StreamType streamType) {
    final ExternalStreamRequest request = of(calendar, oauth2Authorization, stream, streamType);
    request.setVisibility(visibility);
    request.setRequestType(ExternalStreamRequestType.visibilityUpdate());
    return request;
  }

  public static ExternalStreamRequest ofProcessAttendeeJoinRequest(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final StreamAttendee attendee,
                                                                   final StreamType streamType, final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto) {
    final ExternalStreamRequest request = of(calendar, oauth2Authorization, stream, streamType);
    request.setAttendee(attendee);
    request.setProcessAttendeeRequestToJoinStreamDto(processAttendeeRequestToJoinStreamDto);
    request.setRequestType(ExternalStreamRequestType.processAttendeeJoinRequest());
    return request;
  }

  public static ExternalStreamRequest ofJoinStream(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final StreamType streamType, final String attendeeEmailAddress, final JoinStreamDto joinStreamDto) {
    final ExternalStreamRequest request = of(calendar, oauth2Authorization, stream, streamType);
    request.setAttendeeEmailAddress(attendeeEmailAddress);
    request.setJoinStreamDto(joinStreamDto);
    request.setRequestType(ExternalStreamRequestType.joinStream());
    return request;
  }

  public static ExternalStreamRequest of(final Calendar calendar, final Oauth2Authorization oauth2Authorization, final FleenStream stream, final StreamType streamType) {
    final ExternalStreamRequest request = of(calendar, stream, streamType);
    request.setOauth2Authorization(oauth2Authorization);
    return request;
  }

  public static ExternalStreamRequest of(final Calendar calendar, final FleenStream stream, final StreamType streamType) {
    final ExternalStreamRequest request = of();
    request.setCalendar(calendar);
    request.setStream(stream);
    request.setStreamType(streamType);
    return request;
  }

  public static ExternalStreamRequest of() {
    return new ExternalStreamRequest();
  }
}

