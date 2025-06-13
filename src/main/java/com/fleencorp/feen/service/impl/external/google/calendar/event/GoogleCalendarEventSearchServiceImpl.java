package com.fleencorp.feen.service.impl.external.google.calendar.event;

import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.mapper.external.GoogleCalendarEventMapper;
import com.fleencorp.feen.calendar.model.request.event.read.ListCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.read.RetrieveCalendarEventRequest;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleListCalendarEventResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleRetrieveCalendarEventResponse;
import com.fleencorp.feen.service.external.google.calendar.event.GoogleCalendarEventSearchService;
import com.fleencorp.feen.service.report.ReporterService;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.fleencorp.feen.constant.base.ReportMessageType.GOOGLE_CALENDAR;
import static com.fleencorp.feen.mapper.external.GoogleCalendarEventMapper.mapToEventExpanded;
import static com.fleencorp.feen.util.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.toDateTime;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class GoogleCalendarEventSearchServiceImpl implements GoogleCalendarEventSearchService {

  private final Calendar calendar;
  private final ReporterService reporterService;

  /**
   * Constructs a new CalendarService with the specified Calendar instance.
   *
   * @param calendar  the Calendar instance to be used by this service
   * @param reporterService The service used for reporting events.
   */
  public GoogleCalendarEventSearchServiceImpl(
    final Calendar calendar,
    final ReporterService reporterService) {
    this.calendar = calendar;
    this.reporterService = reporterService;
  }


  /**
   * Lists events from Google Calendar based on the provided request parameters.
   *
   * <p>This method retrieves events from the specified calendar within the given time range and
   * based on other criteria provided in the request. If an error occurs during the retrieval process, it is logged.</p>
   *
   * @param listCalendarEventRequest the request object containing various parameters for listing events
   * @return {@link GoogleListCalendarEventResponse} the response containing the events result
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/list">Events: list</a>
   */
  @MeasureExecutionTime
  @Override
  public GoogleListCalendarEventResponse listEvent(final ListCalendarEventRequest listCalendarEventRequest) {
    try {
      // Retrieve events from the calendar based on the request parameters
      final Events events = calendar.events()
        .list(listCalendarEventRequest.getCalendarId())
        .setMaxResults(listCalendarEventRequest.getMaxResultOrLimit())
        .setTimeMin(toDateTime(listCalendarEventRequest.getFrom()))
        .setTimeMax(toDateTime(listCalendarEventRequest.getTo()))
        .setSingleEvents(listCalendarEventRequest.getSingleEvents())
        .setShowDeleted(listCalendarEventRequest.getShowDeleted())
        .setOrderBy(listCalendarEventRequest.getOrderBy())
        .setQ(listCalendarEventRequest.getQ())
        .setPageToken(listCalendarEventRequest.getPageToken())
        .setTimeZone(listCalendarEventRequest.getTimezone())
        .execute();

      return GoogleCalendarEventMapper.mapToCalendarEventResponse(events);
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while listing event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    return GoogleListCalendarEventResponse.of();
  }

  /**
   * Retrieves an event from Google Calendar based on the provided retrieval request.
   *
   * <p>This method retrieves the specified event from the calendar using its ID.
   * If an error occurs during the retrieval process, it is logged.</p>
   *
   * @param retrieveCalendarEventRequest the request object containing the calendar ID and event ID to retrieve
   * @return {@link GoogleRetrieveCalendarEventResponse} the response containing the retrieved event
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/get">Events: get</a>
   */
  @MeasureExecutionTime
  @Override
  public GoogleRetrieveCalendarEventResponse retrieveEvent(final RetrieveCalendarEventRequest retrieveCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the retrieval request
      final String calendarId = retrieveCalendarEventRequest.getCalendarId();
      final String eventId = retrieveCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events()
        .get(calendarId, eventId)
        .execute();

      if (nonNull(event)) {
        return GoogleRetrieveCalendarEventResponse.of(eventId, mapToEventExpanded(event), event);
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot retrieve event. Event does not exist or cannot be found. {}", eventId));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while retrieving the event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    return null;
  }
}
